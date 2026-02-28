package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;
import static fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem.LADLE_CAPACITY;
import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

public class FaucetBlockEntity extends SmartBlockEntity {
    private static final int MAX_HEIGHT = 5;
    public static final int TRANSFER_RATE = 10;

    protected BeltProcessingBehaviour beltProcessing;
    protected boolean beltBehaviorOverride;

    private LazyOptional<IFluidHandler> attachedTank;
    private LazyOptional<IFluidHandler> targetTank;

    private int fallingDistance;

    // Rendering purposes only
    private FluidStack renderedFluid;

    public FaucetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        beltBehaviorOverride = false;

        neighborChanged(getBlockPos());
        attachedTank = LazyOptional.empty();
        targetTank = LazyOptional.empty();

        fallingDistance = 0;
        renderedFluid = FluidStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived)
                .whileItemHeld(this::whenItemHeld);
        behaviours.add(beltProcessing);
    }

    private BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual())
            return PASS;
        if (!GenericItemFilling.canItemBeFilled(level, transported.stack))
            return PASS;

        getAttachedTank();
        if (!attachedTank.isPresent())
            return PASS;

        if (getAttachedTank().getFluidInTank(0).isEmpty())
            return HOLD;

        if (GenericItemFilling.getRequiredAmountForItem(level, transported.stack, getAttachedTank().getFluidInTank(0)) == -1)
            return PASS;

        return HOLD;
    }

    private BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (!beltBehaviorOverride)
            beltBehaviorOverride = true;

        if (!LadleItem.isLadle(transported.stack)) {
            reset();
            return PASS;
        }
        FluidStack fluid = getAttachedTank().getFluidInTank(0);
        if (getAttachedTank().getFluidInTank(0).isEmpty()) {
            updateRenderedFluid(FluidStack.EMPTY);
            return HOLD;
        }
        int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(level, transported.stack, fluid.copy());
        if (requiredAmountForItem == -1) {
            reset();
            return PASS;
        }

        if (requiredAmountForItem > fluid.getAmount())
            return HOLD;

        FluidStack drained = getAttachedTank().drain(TRANSFER_RATE * 5, FluidAction.EXECUTE);
        updateRenderedFluid(drained.copy());
        setFaucetOpen(true);
        updateFallDistance(2);

        if (!drained.isEmpty())
            transported.stack = GenericItemFilling.fillItem(level, drained.getAmount(), transported.stack, drained);
        return HOLD;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {

        int oldFallDist = fallingDistance;
        fallingDistance = compound.getInt("FallingDistance");
        if (oldFallDist != fallingDistance)
            invalidateRenderBoundingBox();

        renderedFluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("RenderedFluid"));
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("FallingDistance", fallingDistance);
        compound.put("RenderedFluid", renderedFluid.writeToNBT(new CompoundTag()));
        super.write(compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();

        if (getBlockState().getValue(FaucetBlock.OPEN)) {
            if (level.isClientSide()) {
                if (!renderedFluid.isEmpty())
                    createFluidParticles(renderedFluid);
                return;
            }

            if (beltBehaviorOverride)
                return;

            for (boolean simulate : Iterate.trueAndFalse) {
                FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
                FluidStack drained = getAttachedTank().drain(TRANSFER_RATE, action);

                if (!simulate)
                    updateRenderedFluid(drained);

                spillOnEntities(drained, action);

                int filled = 0;
                if (drained.getAmount() > 0)
                    filled = tryFill(drained, action);

                if (filled <= 0 && simulate) {
                    if (!getBlockState().getValue(FaucetBlock.POWERED)) {
                        setFaucetOpen(false);
                        break;
                    }
                }
            }
        }
    }

    public boolean canOpenFaucet() {
        FluidAction action = FluidAction.SIMULATE;
        return tryFill(getAttachedTank().drain(TRANSFER_RATE, action), action) > 0;
    }

    public void setFaucetOpen(boolean openState) {
        if (getBlockState().getValue(FaucetBlock.OPEN).equals(openState))
            return;

        BlockState state = getBlockState().setValue(FaucetBlock.OPEN, openState);
        getLevel().setBlockAndUpdate(getBlockPos(), state);
        FaucetBlock.playSound(null, getLevel(), getBlockPos(), openState);
    }

    public LazyOptional<IFluidHandler> getTank(BlockPos pos, Direction direction) {
        BlockEntity attachedBE = level.getBlockEntity(pos);

        if (attachedBE != null) {
            attachedBE.setChanged();
            LazyOptional<IFluidHandler> fluidHandler = attachedBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction);
            if (fluidHandler.isPresent())
                return fluidHandler;
        }

        return LazyOptional.empty();
    }

    public IFluidHandler getAttachedTank() {
        Direction facing = getBlockState().getValue(FaucetBlock.FACING);
        if (!attachedTank.isPresent()) // Fetch the attached tank only if it has changed
            attachedTank = getTank(worldPosition.relative(facing.getOpposite()), facing);
        return attachedTank.orElse(EmptyFluidHandler.INSTANCE);
    }

    public IFluidHandler getTargetTank() {
        if (!targetTank.isPresent()) {
            // Fetch the targeted tank each time needed

            int fallDist = 0;
            BlockPos targetPos = worldPosition;
            for (int i = 0; i < MAX_HEIGHT; i++) {
                targetPos = targetPos.below();
                if (!level.getBlockState(targetPos).isAir()) {
                    fallDist = i + 1;
                    break;
                }
            }
            targetTank = getTank(targetPos, getBlockState().getValue(FaucetBlock.FACING).getOpposite());
            updateFallDistance(fallDist);
            notifyUpdate();
        }

        return targetTank.orElse(EmptyFluidHandler.INSTANCE);
    }

    private void spillOnEntities(FluidStack drained, FluidAction action) {
        Fluid fluid = drained.getFluid();
        List<Entity> entities = getLevel().getEntities(null, getRenderBoundingBox()); // Blacklist entities in the parameter

        if (drained.isEmpty())
            return;

        for (Entity entity : entities) {

            if (entity instanceof LadleEntity ladleEntity) {
                ItemStack ladle = ladleEntity.getBox();
                if (LadleItem.getFluidAmount(ladle) < LADLE_CAPACITY) {
                    FluidTank targetTank = LadleItem.getFluidContents(ladle);
                    if (drained.isEmpty())
                        break;

                    int filled = targetTank.fill(drained.copy(), action);
                    if (action.simulate() || filled <= 0)
                        continue;

                    drained.shrink(filled);
                    LadleItem.setFluidContents(ladle, targetTank);
                }
            }

            if (action.execute()) {
                if (CMFluids.isHotFluid(fluid)) {
                    if (!entity.fireImmune()) {
                        entity.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
                        if (entity.hurt(CMDamageTypes.moltenFluid(entity.level()), 4.0F))
                            entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
                    }
                }
            }
        }
    }

    protected int tryFill(FluidStack drained, FluidAction action) {
        IFluidHandler targetTank = getTargetTank();

        if (drained.isEmpty())
            return 0;

        return targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained.copy(), action)
                : targetTank.fill(drained.copy(), action);
    }

    protected void updateRenderedFluid(FluidStack drained) {
        if (!renderedFluid.isFluidEqual(drained)) {
            renderedFluid = drained;
            notifyUpdate();
        }
    }

    protected void updateFallDistance(int fallDist) {
        if (fallDist != fallingDistance) {
            fallingDistance = fallDist;
            notifyUpdate();
        }
    }

    protected void reset() {
        updateRenderedFluid(FluidStack.EMPTY);
        setFaucetOpen(false);
        beltBehaviorOverride = false;
        updateFallDistance(0);
        notifyUpdate();
    }

    public FluidStack getRenderedFluid() {
        return renderedFluid;
    }

    public int getFallingDistance() {
        return fallingDistance;
    }

    public void neighborChanged(BlockPos neighbor) {
        if (worldPosition.relative(getBlockState().getValue(FaucetBlock.FACING).getOpposite()).equals(neighbor)) {
            attachedTank.invalidate(); // Invalidate previous attached tank
        } else if (worldPosition.below().equals(neighbor)) {
            targetTank.invalidate(); // Invalidate targeted tank if smth change in the path
        }
    }

    private void createFluidParticles(FluidStack fluid) {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof FaucetBlock))
            return;
        Direction direction = blockState.getValue(FaucetBlock.FACING);
        Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 outVec = VecHelper.getCenterOf(worldPosition)
                .add(directionVec.scale(.65)
                        .subtract(directionVec.normalize().scale(10 / 16f)));
        Vec3 outMotion = directionVec.scale(1 / 96f)
                .add(0, -1 / 16f, 0);

        for (int i = 0; i < 2; i++) {
            ParticleOptions fluidParticle = FluidFX.getFluidParticle(fluid);
            Vec3 m = VecHelper.offsetRandomly(outMotion, RandomSource.create(), 1 / 64f);
            level.addAlwaysVisibleParticle(fluidParticle, outVec.x, outVec.y, outVec.z, m.x, m.y, m.z);
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -(getFallingDistance()), 0);
    }
}
