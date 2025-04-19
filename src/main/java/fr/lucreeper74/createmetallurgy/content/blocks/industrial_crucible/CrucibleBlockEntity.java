package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.AllKeys;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryTank;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import fr.lucreeper74.createmetallurgy.utils.CMConnectivityHandler;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.*;

public class CrucibleBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer {
    public static final int MAX_SIZE = 5;
    private static final int MAX_HEIGHT = 4;
    private static final int CAPACITY_FACTOR = 1000;

    protected FoundryTank tankInventory;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected LazyOptional<IItemHandlerModifiable> itemCapability;

    protected boolean updateConnectivity;
    protected boolean updateCapability;
    protected int luminosity;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected int width;
    protected int height;

    public FoundryData foundry;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public CrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        foundry = new FoundryData();
        tankInventory = createTank();
        foundry.createInventory(this, getMaxWidth() * getMaxWidth() * getMaxHeight());
        fluidCapability = LazyOptional.of(() -> tankInventory);
        itemCapability = LazyOptional.of(() -> foundry.inputInv);
        updateConnectivity = false;
        updateCapability = false;
        height = 1;
        width = 1;
        refreshCapability();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    protected FoundryTank createTank() {
        return new FoundryTank(this, CAPACITY_FACTOR, this::onFluidStackChanged);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalSize() * CAPACITY_FACTOR);
            tankInventory.deserializeNBT(compound.getCompound("TankContent"));

            foundry.inputInv.setFirstLimitedSlot(getTotalSize());
            foundry.inputInv.deserializeNBT(compound.getCompound("MeltingInv"));

            if (tankInventory.getFillState() > 1)
                tankInventory.drain(-(tankInventory.getCapacity() - tankInventory.getFillAmount()), IFluidHandler.FluidAction.EXECUTE);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        foundry.read(compound.getCompound("Ladle"));

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController()) {
                tankInventory.setCapacity(CAPACITY_FACTOR * getTotalSize());
                foundry.inputInv.setFirstLimitedSlot(getTotalSize());
            }
            invalidateRenderBoundingBox();
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        compound.put("Ladle", foundry.write());

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));

        if (isController()) {
            compound.put("TankContent", tankInventory.serializeNBT(new CompoundTag()));
            compound.put("MeltingInv", foundry.inputInv.serializeNBT());
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        } else {
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        }
        compound.putInt("Luminosity", luminosity);
        super.write(compound, clientPacket);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        if (!fluidCapability.isPresent())
            refreshCapability();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        CMConnectivityHandler.formMulti(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (updateConnectivity)
            updateConnectivity();

        if (isController()) {
            foundry.tick(this);
        }
    }


    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public void updateState() {
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!CrucibleBlock.isLadle(blockState))
                        continue;

                    CrucibleBlock.Shape shape = CrucibleBlock.Shape.PLAIN;

                    if (width == 1)
                        shape = CrucibleBlock.Shape.PLAIN;

                    if (width != 1)
                        shape = xOffset == 0 ? zOffset == 0 ? CrucibleBlock.Shape.NW :
                                zOffset == width - 1 ? CrucibleBlock.Shape.SW : CrucibleBlock.Shape.WEST :

                                xOffset == width - 1 ? zOffset == 0 ? CrucibleBlock.Shape.NE :
                                        zOffset == width - 1 ? CrucibleBlock.Shape.SE : CrucibleBlock.Shape.EAST :

                                        zOffset == 0 ? CrucibleBlock.Shape.NORTH : zOffset == width - 1 ? CrucibleBlock.Shape.SOUTH : CrucibleBlock.Shape.INNER;

                    level.setBlock(pos, blockState.setValue(CrucibleBlock.SHAPE, shape), 22);
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    private void refreshCapability() {
        LazyOptional<IFluidHandler> oldFCap = fluidCapability;
        fluidCapability = LazyOptional.of(this::handlerForFluidCapability);
        oldFCap.invalidate();

        LazyOptional<IItemHandlerModifiable> oldICap = itemCapability;
        itemCapability = LazyOptional.of(this::handlerForItemCapability);
        oldICap.invalidate();
    }

    private IFluidHandler handlerForFluidCapability() {
        return isController() ? tankInventory
                : getControllerBE() != null ? getControllerBE().handlerForFluidCapability() : new FluidTank(0);
    }

    private IItemHandlerModifiable handlerForItemCapability() {
        return isController() ? foundry.inputInv
                : getControllerBE() != null ? getControllerBE().handlerForItemCapability() : new SmartInventory(0, this, 0, false);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        FluidType attributes = newFluidStack.getFluid()
                .getFluidType();
        int luminosity = (int) (attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();
        int maxY = (int) ((tankInventory.getFillState() * height) + 1);

        for (int yOffset = 0; yOffset < height; yOffset++) {
            boolean isBright = reversed ? (height - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    CrucibleBlockEntity tankAt = CMConnectivityHandler.partAt(getType(), level, pos);
                    if (tankAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, tankAt.getBlockState()
                            .getBlock());
                    if (tankAt.luminosity == actualLuminosity)
                        continue;
                    tankAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }
    }

    protected void setLuminosity(int luminosity) {
        if (level.isClientSide)
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        sendData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CrucibleBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof CrucibleBlockEntity)
            return (CrucibleBlockEntity) blockEntity;
        return null;
    }

    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * CAPACITY_FACTOR);
        foundry.inputInv.setFirstLimitedSlot(blocks);
        int overflow = tankInventory.getFillAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            tankInventory.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
    }


    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    public void updateLadleState(boolean controlled) {
        if (!isController())
            return;

        foundry.setActive(controlled);
        notifyUpdate();
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide)
            return;
        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        BlockState state = getBlockState();
        if (CrucibleBlock.isLadle(state)) {
            state = state.setValue(CrucibleBlock.SHAPE, CrucibleBlock.Shape.PLAIN);
            state = state.setValue(CrucibleBlock.BOTTOM, true);
            state = state.setValue(CrucibleBlock.TOP, true);
            getLevel().setBlock(worldPosition, state, 22);
        }
        setChanged();
        sendData();
    }

    protected void processFallOnEntity(Entity entityIn) {
        if (level == null || level.isClientSide())
            return;

        if (!entityIn.isAlive())
            return;

        EntityMeltingRecipe recipe = null;

        Predicate<Recipe<?>> type = RecipeConditions.isOfType(CMRecipeTypes.ENTITY_MELTING.getType());
        List<Recipe<?>> recipes = RecipeFinder.get(EntityMeltingCacheKey, level, type).stream()
                .filter(r -> r instanceof EntityMeltingRecipe entityRecipe
                        && entityRecipe.matches(this, entityRecipe, entityIn.getType())
                        && FoundryRecipe.isEnoughHeated(this, entityRecipe))
                .toList();

        if (!recipes.isEmpty())
            recipe = (EntityMeltingRecipe) recipes.get(0);

        boolean isFireImmune = entityIn.fireImmune();

        if (!isFireImmune)
            entityIn.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);

        if (recipe != null) {
            if (entityIn.hurt(CMDamageTypes.foundry(level), recipe.getEntityIngredient().getDamage()))
                entityIn.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + RandomSource.create().nextFloat() * 0.4F);

            if (!entityIn.isAlive()) {
                for (FluidStack output : recipe.getFluidResults())
                    if (getTank().fill(output.copy(), IFluidHandler.FluidAction.SIMULATE) >= output.getAmount())
                        getTank().fill(output.copy(), IFluidHandler.FluidAction.EXECUTE);
            }
        } else if (!isFireImmune && foundry.getCurrentHeat() > 0)
            entityIn.hurt(CMDamageTypes.foundry(level), 4.0F);

    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (CrucibleBlock.isLadle(state)) { // safety
            state = state.setValue(CrucibleBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(CrucibleBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController())
            updateState();
        updateLadleState(!foundry.isActive());
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return getMaxHeight();
        return getMaxWidth();
    }

    public FoundryTank getTank() {
        return tankInventory;
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }


    public int getMaxHeight() {
        return MAX_HEIGHT;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    public int getTotalSize() {
        return width * width * height;
    }

    public int getBaseSize() { return getWidth() * getWidth(); }

    public static int getCapacityFactor() {
        return CAPACITY_FACTOR;
    }

    private static final Object EntityMeltingCacheKey = new Object();

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CrucibleBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;

        CMLang.translate("crucible.title").forGoggles(tooltip);

        controllerBE.foundry.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getBaseSize());

        CMLang.translate("crucible.capacity").style(ChatFormatting.GRAY).forGoggles(tooltip);


        FoundryTank tank = controllerBE.getTank();
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");

        CMLang.number(tank.getFillAmount())
                .add(mb)
                .style(ChatFormatting.BLUE)
                .text(ChatFormatting.GRAY, " / ")
                .add(CreateLang.number(tank.getCapacity())
                        .add(mb)
                        .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        tooltip.add(Component.empty());

        if (AllKeys.shiftDown()) {
            CMLang.translate("crucible.fluid_content")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            if (tank.isEmpty())
                CMLang.translate("crucible.empty").style(ChatFormatting.DARK_GRAY).forGoggles(tooltip, 1);
            else
                for (FluidStack fluid : tank.fluids) {
                    CMLang.text("")
                            .add(CMLang.fluidName(fluid)
                                    .add(CMLang.text(" "))
                                    .style(ChatFormatting.DARK_GRAY)
                                    .add(CMLang.number(fluid.getAmount())
                                            .add(mb)
                                            .style(ChatFormatting.BLUE)))
                            .forGoggles(tooltip, 1);
                }
        } else
            tooltip.add(CMLang.translateDirect("crucible.hold_details", Component.translatable("create.tooltip.keyShift").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

        return true;
    }
}
