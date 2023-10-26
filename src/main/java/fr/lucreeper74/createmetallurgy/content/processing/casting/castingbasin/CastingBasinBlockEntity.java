package fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.utils.LANG;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class  CastingBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    public SmartFluidTankBehaviour inputTank;
    public SmartInventory inv;
    public CastingBasinRecipe currentRecipe;
    public boolean running;
    public int processingTick;

    public CastingBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new SmartInventory(1, this, 1, true);
        itemCapability = LazyOptional.of(() -> inv);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));

        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 810, true);
        behaviours.add(inputTank);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("inv", inv.serializeNBT());
        compound.putInt("CastingTime", processingTick);
        compound.putBoolean("Running", running);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inv.deserializeNBT(compound.getCompound("inv"));
        processingTick = compound.getInt("CastingTime");
        running = compound.getBoolean("Running");
        super.read(compound, clientPacket);
    }

    public void readOnlyItems(CompoundTag compound) {
        inv.deserializeNBT(compound.getCompound("inv"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return itemCapability.cast();
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return inputTank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inv);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null) return;

        if (!level.isClientSide && (currentRecipe == null || processingTick == -1)) {
            running = false;
            processingTick = -1;
            startProcess();
        }

        if (running) {
            if (!level.isClientSide) {
                if (canProcess()) {
                    if(processingTick <= 0) {
                        process();
                        level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH,
                                SoundSource.BLOCKS, .2f, .5f);
                    }
                } else {
                    processFailed();
                }
                sendData();
            }
            if (level.isClientSide) spawnParticles();

            if (processingTick >= 0) {
                --processingTick;
            }
        }
    }

    public boolean canProcess() {
        FluidStack fluidInTank = getFluidTank().getFluidInTank(0);
        return currentRecipe.getFluidIngredients().get(0).test(fluidInTank)
                && fluidInTank.getAmount() >= currentRecipe.getFluidIngredients().get(0).getRequiredAmount()
                && inv.isEmpty();
    }

    public void startProcess() {
        if (running && processingTick > 0) return;
        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty()) return;

        currentRecipe = (CastingBasinRecipe) recipes.get(0);

        if(canProcess()) {
            processingTick = currentRecipe.getProcessingDuration();
            running = true;
            sendData();
        }
    }

    public void process() {
        FluidStack fluidInTank = getFluidTank().getFluidInTank(0);
        inv.insertItem(0, currentRecipe.getResultItem().copy(), false);
        fluidInTank.shrink(currentRecipe.getFluidIngredients().get(0).getRequiredAmount());
        getBehaviour(SmartFluidTankBehaviour.INPUT)
                .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);

        processingTick = -1;
        currentRecipe = null;
        running = false;
    }

    public void processFailed() {
        processingTick = -1;
        currentRecipe = null;
        running = false;
    }

    protected void spawnParticles() {
        RandomSource r = level.getRandom();
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                .multiply(1, 0, 1));
        if (r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y + .45, v.z, 0, 0, 0);
    }

    protected List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> list = RecipeFinder.get(getRecipeCacheKey(), level, this::matchStaticFilters);
        return list.stream()
                .filter(recipe -> {
                    if (recipe instanceof CastingBasinRecipe castingRecipe) {
                        return castingRecipe.getFluidIngredients().get(0).test(getFluidTank().getFluidInTank(0));
                    }
                    return false;
                })
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .collect(Collectors.toList());
    }

    public IFluidHandler getFluidTank() {
        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(new FluidTank(1));
    }

    protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
        return r.getType() == AllRecipeTypes.CASTING_IN_BASIN.getType();
    }

    private static final Object CastingInBasinRecipesKey = new Object();

    protected Object getRecipeCacheKey() {
        return CastingInBasinRecipesKey;
    }


    // CLIENT THINGS -----------------
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LANG.translate("gui.goggles.castingbasin_contents")
                .forGoggles(tooltip);

        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());
        IFluidHandler fluids = getFluidTank();
        boolean isEmpty = true;

        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            LANG.text("")
                    .add(Components.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(LANG.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = LANG.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluids.getTanks(); i++) {
            FluidStack fluidStack = fluids.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;
            LANG.text("")
                    .add(LANG.fluidName(fluidStack)
                            .add(LANG.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(LANG.number(fluidStack.getAmount())
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.remove(0);

        return true;
    }
}