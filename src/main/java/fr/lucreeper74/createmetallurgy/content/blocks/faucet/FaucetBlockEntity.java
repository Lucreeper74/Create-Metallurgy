package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.content.fluids.FluidFX;
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

import static fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem.LADLE_CAPACITY;
import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

public class FaucetBlockEntity extends SmartBlockEntity {
    private static final int MAX_HEIGHT = 5;
    public static final int TRANSFER_RATE = 10;

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

    public IFluidHandler getAttachedTank() {
        Direction facing = getBlockState().getValue(FaucetBlock.FACING);
        if (attachedTank == null) // Fetch the attached tank only if it has changed
            attachedTank = getTank(worldPosition.relative(facing.getOpposite()), facing);
        return attachedTank.orElse(EmptyFluidHandler.INSTANCE);
    }

    public IFluidHandler getTargetTank() {
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
        return targetTank.orElse(EmptyFluidHandler.INSTANCE);
    }

    public void trySpoutput() {
        if (!renderFluid.isEmpty()) {
            if (level.isClientSide()) {
                createFluidParticles(renderFluid);
                return;
            }
            if (spillOnEntities())
                return; // The fluid is blocked by an entity (e.g Filling ladle)
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

    private boolean spillOnEntities() {
        boolean isFluidBlocked = false;

        Fluid spilledFluid = renderFluid.getFluid();
        List<Entity> entities = getLevel().getEntities(null, getRenderBoundingBox()); // Blacklist entities in the parameter

        for (Entity entity : entities) {
            if (CMFluids.isMoltenMaterial(spilledFluid)) {
                if (!entity.fireImmune()) {
                    entity.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
                    if (entity.hurt(CMDamageTypes.moltenFluid(entity.level()), 4.0F))
                        entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
                }
            }

            if (entity instanceof LadleEntity ladleEntity) {
                ItemStack ladle = ladleEntity.getBox();

                if (LadleItem.getFluidAmount(ladle) < LADLE_CAPACITY) {
                    isFluidBlocked = true; // The Ladle can be filled

                    FluidTank targetTank = LadleItem.getFluidContents(ladle);

                    for (boolean simulate : Iterate.trueAndFalse) {
                        FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
                        FluidStack drained = getAttachedTank().drain(TRANSFER_RATE, action);

                        if (drained.isEmpty())
                            break;

                        int filled = targetTank.fill(drained.copy(), action);
                        if (filled <= 0)
                            break;
                        if (simulate)
                            continue;

                        LadleItem.setFluidContents(ladle, targetTank);
                    }
                }
            }
        }
        return isFluidBlocked;
    }

    protected int tryFill() {
        IFluidHandler inputTank = getAttachedTank();
        IFluidHandler targetTank = getTargetTank();

        int filled = 0;
        for (boolean simulate : Iterate.trueAndFalse) {
            FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
            FluidStack drained = inputTank.drain(TRANSFER_RATE, action);

            if (drained.isEmpty())
                return 0;

            filled = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained.copy(), action)
                    : targetTank.fill(drained.copy(), action);
            if (filled <= 0)
                break;
            if (simulate)
                continue;

            if (!renderFluid.isFluidEqual(drained)) {
                renderFluid = drained;
                sendData();
            }
        }

        return filled;
    }

    protected void spillFluid() {
        IFluidHandler inputTank = getAttachedTank();

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
