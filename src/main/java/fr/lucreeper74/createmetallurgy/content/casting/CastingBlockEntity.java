package fr.lucreeper74.createmetallurgy.content.casting;

import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CastingBlockEntity extends SmartBlockEntity {

    public LazyOptional<IItemHandlerModifiable> itemCapability;
    public SmartFluidTankBehaviour inputTank;
    public SmartInventory inv;
    public SmartInventory moldInv;
    protected CastingRecipe currentRecipe;
    public boolean running;
    public int processingTick;

    public CastingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new SmartInventory(1, this, 1, true).forbidInsertion();
        moldInv = new SmartInventory(1, this, 1, true);
        itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inv, moldInv));
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("moldInv", moldInv.serializeNBT());
        compound.put("inv", inv.serializeNBT());
        compound.putInt("castingTime", processingTick);
        compound.putBoolean("running", running);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        moldInv.deserializeNBT(compound.getCompound("moldInv"));
        inv.deserializeNBT(compound.getCompound("inv"));
        processingTick = compound.getInt("castingTime");
        running = compound.getBoolean("running");
        super.read(compound, clientPacket);
    }

    public void readOnlyItems(CompoundTag compound) {
        inv.deserializeNBT(compound.getCompound("inv"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return inputTank.getCapability().cast();
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

        if (level == null) return;

        if (!level.isClientSide && (currentRecipe == null || processingTick == -1)) {
            running = false;
            processingTick = -1;
            startProcess();
        }

        if (running) {
            if (!level.isClientSide) {
                if (matchCastingRecipe(currentRecipe)) {
                    if (processingTick <= 0) {
                        process();
                        level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH,
                                SoundSource.BLOCKS, .2f, .5f);
                    }
                } else {
                    processFailed();
                }
            }
            if (level.isClientSide) spawnParticles();

            if (processingTick >= 0) {
                --processingTick;
            }
        }
    }

    public void startProcess() {
        if (running && processingTick > 0) return;
        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty()) return;

        currentRecipe = (CastingRecipe) recipes.get(0);

        if (matchCastingRecipe(currentRecipe)) {
            processingTick = isInAirCurrent(this.getLevel(), this.getBlockPos(), this) ?
                    currentRecipe.getProcessingDuration() / 2 : currentRecipe.getProcessingDuration();
            running = true;
            sendData();
        }
    }

    public void process() {
        FluidStack fluidInTank = getFluidTank().getFluidInTank(0);
        inv.setStackInSlot(0, currentRecipe.getResultItem().copy());
        fluidInTank.shrink(currentRecipe.getFluidIngredient().getRequiredAmount());
        getBehaviour(SmartFluidTankBehaviour.INPUT)
                .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);

        if(currentRecipe.isMoldConsumed())
            moldInv.setStackInSlot(0, ItemStack.EMPTY);

        processingTick = -1;
        currentRecipe = null;
        running = false;
        sendData();
    }

    public void processFailed() {
        processingTick = -1;
        currentRecipe = null;
        running = false;
        sendData();
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
        return getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(new FluidTank(1));
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

    protected abstract <C extends Container> boolean matchStaticFilters(Recipe<C> recipe);

    protected abstract Object getRecipeCacheKey();
}