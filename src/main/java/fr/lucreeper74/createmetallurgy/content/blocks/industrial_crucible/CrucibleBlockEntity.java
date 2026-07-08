package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.AllKeys;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryData;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryItemSlot;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryTank;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.MobMeltingRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import fr.lucreeper74.createmetallurgy.utils.CMConnectivityHandler;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import fr.lucreeper74.createmetallurgy.utils.SideAttachment;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock.*;
import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

public class CrucibleBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer, Clearable {
    protected Map<Direction, SideAttachment> attachmentMap = new EnumMap<>(Direction.class);

    public FoundryTank tankInventory;
    protected IFluidHandler fluidCapability;

    protected FoundryItemSlot foundrySlot;
    protected IItemHandlerModifiable itemCapability;

    protected boolean updateConnectivity;
    protected boolean updateCapability;
    protected int luminosity;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected int width;
    protected int height;

    public FoundryData foundryData;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public CrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        for (Direction dir : Direction.values()) {
            attachmentMap.put(dir, SideAttachment.NONE);
        }

        tankInventory = new FoundryTank(getCapacityPerBlock(), this::onFluidContentChanged);

        foundrySlot = new FoundryItemSlot(this::getControllerBE, () -> {
            refreshCapability(false, true);
            notifyUpdate();
        });

        updateConnectivity = false;
        updateCapability = false;
        height = 1;
        width = 1;

        foundryData = new FoundryData(this);

        refreshCapability(true, false);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.INDUSTRIAL_CRUCIBLE.get(),
                (be, context) -> {
                    if (be.itemCapability == null)
                        be.refreshCapability(false, true);
                    return be.itemCapability;
                });

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMBlockEntityTypes.INDUSTRIAL_CRUCIBLE.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability(true, false);
                    return be.fluidCapability;
                });
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;

        updateConnectivity = compound.contains("Uninitialized");
        luminosity = compound.getInt("Luminosity");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalSize() * getCapacityPerBlock());
            tankInventory.deserializeNBT(registries, compound.getCompound("TankContent"), clientPacket);
            foundryData.read(compound.getCompound("FoundryData"), getWidth());

            if (tankInventory.getFillState() > 1)
                tankInventory.drain(tankInventory.getFillAmount() - tankInventory.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
        }

        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        updateCapability = true;
        foundrySlot.deserializeNBT(registries, compound.getCompound("FoundrySlot"), clientPacket);

        attachmentMap.clear();
        CompoundTag modesTag = compound.getCompound("SideModes");
        for (Direction dir : Direction.values()) {
            String modeName = modesTag.getString(dir.getName());
            attachmentMap.put(dir, SideAttachment.byName(modeName));
        }

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityPerBlock() * getTotalSize());
            invalidateRenderBoundingBox();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));

        if (isController()) {
            compound.put("TankContent", tankInventory.serializeNBT(registries));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
            compound.put("FoundryData", foundryData.write());
        } else
            compound.put("Controller", NbtUtils.writeBlockPos(controller));

        compound.put("FoundrySlot", foundrySlot.serializeNBT(registries));

        compound.putInt("Luminosity", luminosity);

        CompoundTag modesTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            modesTag.putString(dir.getName(), attachmentMap.get(dir).getSerializedName());
        }
        compound.put("SideModes", modesTag);

        super.write(compound, registries, clientPacket);
    }

    public SideAttachment getSideAttachment(Direction side) {
        return attachmentMap.getOrDefault(side, SideAttachment.NONE);
    }

    public boolean canSetAttachment(Direction side, SideAttachment attachment) {
        BlockState blockState = level.getBlockState(getBlockPos());

        if (attachment.equals(SideAttachment.NONE))
            return true; // Cannot prevent from clearing a side attachment

        if (blockState.getValue(SHAPE).equals(CrucibleBlock.Shape.INNER) && (!blockState.getValue(BOTTOM) || !side.equals(Direction.DOWN)))
            return false; // Cannot place attachment on inner blocks (except bottom ones)

        if (!attachmentMap.get(side).equals(SideAttachment.NONE))
            return false; // Cannot swap between attachments, need to remove it first

        if (!attachment.equals(SideAttachment.GAUGE) && attachmentMap.containsValue(attachment))
            return false; // Cannot have the same attachment twice on the same block (except Gauges)

        if (side.equals(Direction.UP))
            return false; // Cannot have attachment on the top side

        if (attachment.equals(SideAttachment.GAUGE) && side.equals(Direction.DOWN))
            return false; // Cannot have gauges on the bottom side

        return true;
    }

    public boolean setAttachment(Direction side, SideAttachment mode, boolean simulate) {
        if (!canSetAttachment(side, mode))
            return false;

        if (!simulate) {
            attachmentMap.put(side, mode);
            setChanged();
        }
        return true;
    }

    private void refreshCapability(boolean fluidCap, boolean itemCap) {
        if (fluidCap) {
            fluidCapability = handlerForFluidCapability();
            invalidateCapabilities();
        }

        if (itemCap) {
            if (!isController()) {
                CrucibleBlockEntity controllerBE = getControllerBE();
                if (controllerBE == null)
                    return;
                controllerBE.refreshCapability(true, true);
                itemCapability = controllerBE.itemCapability;
                return;
            }

            foundryData.getInputInv().clear();
            for (int yOffset = 0; yOffset < height; yOffset++) {
                for (int xOffset = 0; xOffset < width; xOffset++) {
                    for (int zOffset = 0; zOffset < width; zOffset++) {
                        BlockPos cruciblePos = this.worldPosition.offset(xOffset, yOffset, zOffset);

                        CrucibleBlockEntity crucibleAt = ConnectivityHandler.partAt(CMBlockEntityTypes.INDUSTRIAL_CRUCIBLE.get(), getLevel(), cruciblePos);

                        if (crucibleAt != null)
                            foundryData.getInputInv().addSlot(crucibleAt.foundrySlot);
                    }
                }
            }

            itemCapability = foundryData.getInputInv();
        }
    }

    private IFluidHandler handlerForFluidCapability() {
        return isController() ? tankInventory
                : getControllerBE() != null ? getControllerBE().handlerForFluidCapability() : new FluidTank(0);
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        updateFoundryTemperature();
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
        else if (!lastKnownPos.equals(worldPosition)) {
            onPositionChanged();
            return;
        }

        if (updateCapability) {
            updateCapability = false;
            refreshCapability(true, true);
        }
        if (updateConnectivity)
            updateConnectivity();

        if (isController()) {
            foundryData.tick();

            if (level.isClientSide)
                tankInventory.tick();
        }
    }

    public void updateFoundryTemperature() {
        CrucibleBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.foundryData.needsHeatLevelUpdate = true;
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

    public void updateBlockState() {
        if (level == null)
            return;

        BlockState blockState = getBlockState();
        if (!CrucibleBlock.isCrucible(blockState))
            return; // For safety

        CrucibleBlock.Shape newShape = getShape();

        // Update block state values
        blockState = blockState.setValue(CrucibleBlock.BOTTOM, getController().getY() == getBlockPos().getY());
        blockState = blockState.setValue(TOP, getController().getY() + height - 1 == getBlockPos().getY());
        blockState = blockState.setValue(CrucibleBlock.SHAPE, newShape);

        level.setBlockAndUpdate(getBlockPos(), blockState);
        level.getChunkSource()
                .getLightEngine()
                .checkBlock(getBlockPos());

        if (newShape.equals(CrucibleBlock.Shape.INNER)) {
            // Drop Attachments
            for (Direction side : Iterate.directions) {
                SideAttachment mode = getSideAttachment(side);
                if (mode.equals(SideAttachment.NONE))
                    continue; // Avoid useless operations

                if (side.equals(Direction.DOWN) && blockState.getValue(BOTTOM))
                    continue; // Keep bottom attachments

                if (side.equals(Direction.UP) && blockState.getValue(TOP))
                    continue; // Keep top attachments

                if (setAttachment(side, SideAttachment.NONE, false))
                    Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), mode.getItem());
            }
        }
    }

    private CrucibleBlock.Shape getShape() {
        CrucibleBlock.Shape shape = CrucibleBlock.Shape.PLAIN;

        CrucibleBlockEntity controller = getControllerBE();

        if (controller == null)
            return shape;

        int xOffset = getBlockPos().getX() - controller.getBlockPos().getX();
        int zOffset = getBlockPos().getZ() - controller.getBlockPos().getZ();

        if (width != 1) {
            if (xOffset == 0) {
                if (zOffset == 0)
                    return CrucibleBlock.Shape.NW;
                else if (zOffset == (width - 1))
                    return CrucibleBlock.Shape.SW;
                else
                    return CrucibleBlock.Shape.WEST;


            } else if (xOffset == (width - 1)) {
                if (zOffset == 0)
                    return CrucibleBlock.Shape.NE;
                else if (zOffset == (width - 1))
                    return CrucibleBlock.Shape.SE;
                else
                    return CrucibleBlock.Shape.EAST;

            } else if (zOffset == 0)
                return CrucibleBlock.Shape.NORTH;
            else if (zOffset == (width - 1))
                return CrucibleBlock.Shape.SOUTH;
            else
                return CrucibleBlock.Shape.INNER;
        }

        return shape;
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    protected void onFluidContentChanged() {
        if (!hasLevel())
            return;

        int luminosity = 0;
        for (FluidStack fluid : getTank().getFluids()) {
            FluidType type = fluid.getFluid().getFluidType();
            int fluidLum = (int) (type.getLightLevel(fluid) / 1.2f);

            if (fluidLum > luminosity)
                luminosity = fluidLum;
        }

        int maxY = (int) ((tankInventory.getFillState() * height) + 1);
        for (int yOffset = 0; yOffset < height; yOffset++) {
            boolean isBright = (yOffset < maxY);
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

        if (!level.isClientSide)
            notifyUpdate();
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
        if (blockEntity instanceof CrucibleBlockEntity crucibleBE)
            return crucibleBE;
        return null;
    }

    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * CMConfig.server().crucibleCapacity.get() * 1000);

        // Handle Fluid overflow
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

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability(true, true);
        notifyUpdate();
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
        if (CrucibleBlock.isCrucible(state)) {
            state = state.setValue(CrucibleBlock.SHAPE, CrucibleBlock.Shape.PLAIN);
            state = state.setValue(CrucibleBlock.BOTTOM, true);
            state = state.setValue(TOP, true);
            getLevel().setBlock(worldPosition, state, 22);
        }
        notifyUpdate();
    }

    protected void processFallOnEntity(Entity entityIn) {
        if (level == null || level.isClientSide())
            return;

        if (!entityIn.isAlive())
            return;

        MobMeltingRecipe recipe = null;

        Predicate<RecipeHolder<? extends Recipe<?>>> type = RecipeConditions.isOfType(CMRecipeTypes.ENTITY_MELTING.getType());
        List<RecipeHolder<? extends Recipe<?>>> recipes = RecipeFinder.get(EntityMeltingCacheKey, level, type).stream()
                .filter(r -> r.value() instanceof MobMeltingRecipe entityRecipe
                        && entityRecipe.matches(this, entityIn.getType()))
                .toList();

        if (!recipes.isEmpty())
            recipe = (MobMeltingRecipe) recipes.getFirst().value();

        boolean isFireImmune = entityIn.fireImmune();

        if (!isFireImmune && foundryData.getCurrentHeat() > 0)
            entityIn.setRemainingFireTicks(MOLTEN_FLUID_BURNING_TIME);

        if (recipe != null) {
            if (entityIn.hurt(CMDamageTypes.foundry(level), recipe.getEntityIngredient().getDamage()))
                entityIn.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + RandomSource.create().nextFloat() * 0.4F);

            if (!entityIn.isAlive())
                applyRecipe(recipe);
        } else if (!isFireImmune && foundryData.getCurrentHeat() > 0)
            entityIn.hurt(CMDamageTypes.foundry(level), 4.0F);

    }

    private void applyRecipe(MobMeltingRecipe recipe) {
        // TODO: prevent the recipe from process if cannot apply like BasinRecipe

        Ingredient:
        for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            int amountRequired = fluidIngredient.amount();

            for (int i = 0; i < getTank().getTanks(); i++) {
                FluidStack availableFluid = getTank().getFluidInTank(i).copy();
                int availableAmount = availableFluid.getAmount();

                if (fluidIngredient.test(availableFluid)) {
                    availableFluid.setAmount(Math.min(amountRequired, availableAmount));
                    getTank().drain(availableFluid, IFluidHandler.FluidAction.EXECUTE);
                    continue Ingredient;
                }
            }
            return; // Not enough fluid or fluid not match
        }

        // Apply fluids results
        for (FluidStack output : recipe.getFluidResults()) {
            if (getTank().fill(output.copy(), IFluidHandler.FluidAction.SIMULATE) == output.getAmount())
                getTank().fill(output.copy(), IFluidHandler.FluidAction.EXECUTE);
        }

        // Apply results
        for (ItemStack result : recipe.rollResults(getLevel().getRandom())) {
            if (result.isEmpty())
                continue;

            ItemHandlerHelper.insertItemStacked(itemCapability, result.copy(), false);
        }
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        updateBlockState();
        setChanged();
    }

    @Override
    public void clearContent() {
        foundrySlot.removeStack();
        attachmentMap.clear();
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
        return CMConfig.server().crucibleMaxWidth.get();
    }


    public int getMaxHeight() {
        return CMConfig.server().crucibleMaxHeight.get();
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

    public int getBaseSize() {
        return getWidth() * getWidth();
    }

    public static int getCapacityFactor() {
        return CMConfig.server().crucibleCapacity.get();
    }

    public static int getCapacityPerBlock() {
        return getCapacityFactor() * 1000;
    }

    private static final Object EntityMeltingCacheKey = new Object();

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CrucibleBlockEntity controllerBE = getControllerBE();

        CMLang.translate("crucible.title").forGoggles(tooltip);

        if (controllerBE != null)
            controllerBE.foundryData.addToGoggleTooltip(tooltip, attachmentMap.containsValue(SideAttachment.GAUGE), controllerBE.getBaseSize());
        else
            return false;

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
                for (FluidStack fluid : tank.getFluids()) {
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
