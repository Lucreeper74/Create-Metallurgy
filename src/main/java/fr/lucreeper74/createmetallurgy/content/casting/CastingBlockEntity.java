package fr.lucreeper74.createmetallurgy.content.casting;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CastingBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public LazyOptional<IItemHandlerModifiable> itemCapability;
    public CastingFluidTank inputTank;
    private final LazyOptional<CastingFluidTank> fluidCapability = LazyOptional.of(() -> inputTank);
    public SmartInventory inv;
    public SmartInventory moldInv;
    protected CastingRecipe currentRecipe;
    protected FluidStack fluidBuffer;
    public boolean running;
    public int processingTick;

    public CastingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new SmartInventory(1, this, 1, true).forbidInsertion();
        moldInv = new SmartInventory(1, this, 1, true);
        itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inv, moldInv));
        inputTank = new CastingFluidTank(this);
        fluidBuffer = FluidStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("moldInv", moldInv.serializeNBT());
        compound.put("inv", inv.serializeNBT());
        compound.put("inputTank", inputTank.writeToNBT(new CompoundTag()));
        compound.put("fluidBuffer", fluidBuffer.writeToNBT(new CompoundTag()));
        compound.putInt("castingTime", processingTick);
        compound.putBoolean("running", running);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        moldInv.deserializeNBT(compound.getCompound("moldInv"));
        inv.deserializeNBT(compound.getCompound("inv"));
        inputTank.readFromNBT(compound.getCompound("inputTank"));
        fluidBuffer = FluidStack.loadFluidStackFromNBT(compound.getCompound("fluidBuffer"));
        processingTick = compound.getInt("castingTime");
        running = compound.getBoolean("running");
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
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inv);
        ItemHelper.dropContents(level, worldPosition, moldInv);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null)
            return;

        inputTank.tick();

        if (!level.isClientSide && !running) {
            processingTick = -1;
            startProcess();
        }

        if (running) {
            if (currentRecipe == null) { //Was running on unload, fetch old recipe
                List<Recipe<?>> recipes = getMatchingRecipes();
                if (!recipes.isEmpty())
                    currentRecipe = (CastingRecipe) recipes.get(0);
            }

            if (!level.isClientSide) {
                if (canProcess()) {
                    if (processingTick <= 0)
                        process();
                } else
                    reset();
            } else
                spawnParticles();

            if (processingTick >= 0) {
                if (isInAirCurrent(this.getLevel(), this.getBlockPos(), this))
                    processingTick = processingTick - 2;
                else
                    --processingTick;
            }
        }
    }

    public void startProcess() {
        if (running && processingTick > 0)
            return;

        if (canProcess()) {
            processingTick = currentRecipe.getProcessingDuration();
            running = true;
            sendData();
        }
    }

    public void process() {
        FluidStack fluidInTank = getFluidTank().getFluidInTank(0);
        inv.setStackInSlot(0, currentRecipe.getResultItem().copy());
        fluidInTank.shrink(currentRecipe.getFluidIngredient().getRequiredAmount());

        if (currentRecipe.isMoldConsumed())
            moldInv.setStackInSlot(0, ItemStack.EMPTY);

        level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH,
                SoundSource.BLOCKS, .2f, .5f);
        reset();
    }

    public boolean canProcess() {
        if (currentRecipe != null)
            return inputTank.getFluidAmount() >= inputTank.getCapacity() && matchCastingRecipe(currentRecipe);
        return false;
    }

    protected void spawnParticles() {
        RandomSource r = level.getRandom();
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                .multiply(1, 0, 1));
        if (r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.SMOKE, v.x, v.y + .45, v.z, 0, 0, 0);
    }

    public IFluidHandler getFluidTank() {
        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(new FluidTank(1));
    }

    public static boolean isInAirCurrent(Level level, BlockPos pos, BlockEntity be) {
        int range = 3;

        for (Direction direction : Direction.values()) {
            for (int i = 0; i <= range; i++) {
                BlockPos nearbyPos = pos.relative(direction, i);
                BlockState nearbyState = level.getBlockState(nearbyPos);

                if (nearbyState.getBlock() instanceof EncasedFanBlock) {
                    EncasedFanBlockEntity fanBe = (EncasedFanBlockEntity) level.getBlockEntity(nearbyPos);
                    Direction facing = nearbyState.getValue(EncasedFanBlock.FACING);
                    BlockEntity facingBe = level.getBlockEntity(nearbyPos.relative(facing, i));
                    float flowDist = fanBe.airCurrent.maxDistance;

                    if (be == facingBe && flowDist != 0 && flowDist >= i - 1) return true;
                }
            }
        }
        return false;
    }

    protected <C extends Container> boolean matchCastingRecipe(Recipe<C> recipe) {
        if (recipe == null || !inv.getStackInSlot(0).isEmpty())
            return false;
        return CastingRecipe.match(this, recipe);
    }

    public List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> list = RecipeFinder.get(getRecipeCacheKey(), level, this::matchStaticFilters);
        return list.stream()
                .filter(this::matchCastingRecipe)
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .collect(Collectors.toList());
    }

    public int initProcess(FluidStack fluid, IFluidHandler.FluidAction action) {
        if (currentRecipe != null || running)
            return 0;

        fluidBuffer = fluid;

        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty())
            return 0;

        CastingRecipe recipe = (CastingRecipe) recipes.get(0);
        if (action == IFluidHandler.FluidAction.EXECUTE) {
            currentRecipe = recipe;
            sendData();
        }

        return recipe.getFluidIngredient().getRequiredAmount();
    }

    public void reset() {
        inputTank.reset();
        processingTick = -1;
        currentRecipe = null;
        running = false;
        sendData();
    }

    public FluidStack getFluidBuffer() {
        return fluidBuffer;
    }


    protected abstract <C extends Container> boolean matchStaticFilters(Recipe<C> recipe);

    protected abstract Object getRecipeCacheKey();
}