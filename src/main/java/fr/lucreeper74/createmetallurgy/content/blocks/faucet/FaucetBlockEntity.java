package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraftforge.fluids.capability.IFluidHandler.*;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

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
        fallingDistance = compound.getInt("fallingDistance");

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
        int fallDist = 0;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            pos = pos.below();
            fallDist = i + 1;
            if (!level.getBlockState(pos).isAir())
                break;
        }
        targetTank = getTank(pos, getBlockState().getValue(FaucetBlock.FACING).getOpposite());
        if (fallDist != fallingDistance) {
            fallingDistance = fallDist;
            invalidateRenderBoundingBox();
        }
        sendData();
        return targetTank;
    }

    public void trySpoutput() {
        if (!renderFluid.isEmpty()) {
            if (level.isClientSide()) {
                createFluidParticles(renderFluid);
                return;
            }
            Fluid fluid = renderFluid.getFluid();
            if (fluid.is(CMFluids.MOLTEN_MATERIALS) || fluid.is(FluidTags.LAVA))
                hurtEntities();
        }

        if (tryFill() <= 0) {
            if (getBlockState().getValue(FaucetBlock.POWERED))
                spillFluid(); // If it fails & forced open, spill the fluid
            else {
                BlockState newState = getBlockState().cycle(FaucetBlock.OPEN);
                getLevel().setBlock(getBlockPos(), newState, 3); // Close it
                FaucetBlock.playSound(null, getLevel(), getBlockPos(), newState.getValue(FaucetBlock.OPEN));
            }
        }
    }

    private void hurtEntities() {
        List<Entity> entities = getLevel().getEntities(null, getRenderBoundingBox()); // Blacklist entities in the parameter
        for (Entity entity : entities) {
            if (!entity.fireImmune()) {
                entity.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
                if (entity.hurt(CMDamageTypes.moltenFluid(entity.level()), 4.0F))
                    entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
            }
        }
    }

    protected int tryFill() {
        IFluidHandler inputTank = getAttachedTank().orElse(EmptyFluidHandler.INSTANCE);
        IFluidHandler targetTank = getTargetTank().orElse(EmptyFluidHandler.INSTANCE);

        FluidStack drained = inputTank.drain(TRANSFER_RATE, FluidAction.SIMULATE);

        if (drained.isEmpty())
            return 0;

        int filled = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained, FluidAction.SIMULATE)
                : targetTank.fill(drained, FluidAction.SIMULATE);

        if (filled > 0) {
            drained = inputTank.drain(filled, FluidAction.EXECUTE);
            filled = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained, FluidAction.EXECUTE)
                    : targetTank.fill(drained, FluidAction.EXECUTE);

            if (!renderFluid.isFluidEqual(drained)) {
                renderFluid = drained;
                sendData();
            }
        }

        return filled;
    }

    protected void spillFluid() {
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
