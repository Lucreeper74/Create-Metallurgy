package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.List;

public class FaucetBlockEntity extends SmartBlockEntity {
    private static final int MAX_HEIGHT = 5;
    public static final int TRANSFER_RATE = 5;

    private LazyOptional<IFluidHandler> attachedTank;
    private LazyOptional<IFluidHandler> targetTank;

    // Rendering purposes only
    private int fallingDistance;
    private FluidStack renderFluid = FluidStack.EMPTY;

    public FaucetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        int prevFallingDist = fallingDistance;
        fallingDistance = compound.getInt("fallingDistance");
        if (fallingDistance != prevFallingDist)
            invalidateRenderBoundingBox();

        if (compound.contains("renderFluid")) {
            renderFluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("renderFluid"));
        } else {
            renderFluid = FluidStack.EMPTY;
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("fallingDistance", fallingDistance);
        if (!renderFluid.isEmpty())
            compound.put("renderFluid", renderFluid.writeToNBT(new CompoundTag()));
    }

    @Override
    public void tick() {
        super.tick();

        if (getBlockState().getValue(FaucetBlock.OPEN)) {
            trySpoutput();
        }
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

    public LazyOptional<IFluidHandler> getAttachedTank() {
        Direction facing = getBlockState().getValue(FaucetBlock.FACING);
        if (attachedTank == null) // Fetch the attached tank only if it has changed
            attachedTank = getTank(worldPosition.relative(facing.getOpposite()), facing);
        return attachedTank;
    }

    public LazyOptional<IFluidHandler> getTargetTank() {
        // Fetch the targeted tank each time needed
        BlockPos pos = worldPosition;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            pos = pos.below();
            if (!level.getBlockState(pos).isAir())
                break;
            fallingDistance = i + 2;
        }
        targetTank = getTank(pos, getBlockState().getValue(FaucetBlock.FACING).getOpposite());
        sendData();
        return targetTank;
    }

    public void trySpoutput() {
        if (level.isClientSide()) {
            if (!renderFluid.isEmpty())
                createFluidParticles(renderFluid);
            return;
        }

        if (tryFill() <= 0)
            spillFluid(); // If it fails, spill the fluid
    }

    private int tryFill() {
        IFluidHandler inputTank = getAttachedTank().orElse(EmptyFluidHandler.INSTANCE);
        IFluidHandler targetTank = getTargetTank().orElse(EmptyFluidHandler.INSTANCE);

        FluidStack fluidInTank = inputTank.getFluidInTank(0).copy();
        if (fluidInTank.isEmpty())
            return 0;

        fluidInTank.setAmount(TRANSFER_RATE);

        int fill = 0;

        DirectBeltInputBehaviour directBeltInputBehaviour =
                BlockEntityBehaviour.get(level, getBlockPos().below(fallingDistance), DirectBeltInputBehaviour.TYPE);
        if (directBeltInputBehaviour == null || !directBeltInputBehaviour.canInsertFromSide(Direction.DOWN))
            return 0;

        for (boolean simulate : Iterate.trueAndFalse) {
            IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidInTank, action)
                    : targetTank.fill(fluidInTank, action);

            if (fill <= 0)
                break;
            if (simulate)
                continue;

            FluidStack drained = inputTank.drain(fill, IFluidHandler.FluidAction.EXECUTE);

            if (!renderFluid.isFluidEqual(drained)) {
                renderFluid = drained;
                sendData();
            }
        }
        return fill;
    }

    public void spillFluid() {
        IFluidHandler inputTank = getAttachedTank().orElse(EmptyFluidHandler.INSTANCE);

        FluidStack fluid = inputTank.drain(TRANSFER_RATE, IFluidHandler.FluidAction.EXECUTE);

        if (!renderFluid.isFluidEqual(fluid)) {
            renderFluid = fluid;
            sendData();
        }
    }

    public FluidStack getRenderFluid() {
        return renderFluid;
    }

    public int getFallingDistance() {
        return fallingDistance;
    }

    public void neighborChanged(BlockPos neighbor) {
        if (worldPosition.relative(getBlockState().getValue(FaucetBlock.FACING).getOpposite()).equals(neighbor)) {
            attachedTank = null; // Invalidate previous attached tank
        } else if (worldPosition.below().equals(neighbor)) {
            targetTank = null; // Invalidate targeted tank if smth change in the path
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
