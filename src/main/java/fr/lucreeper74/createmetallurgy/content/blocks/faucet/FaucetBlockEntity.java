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
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;
import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

public class FaucetBlockEntity extends SmartBlockEntity {
    private static final int MAX_HEIGHT = 5;
    public static final int TRANSFER_RATE = 10;

    protected BeltProcessingBehaviour beltProcessing;
    protected boolean beltBehaviorOverride;

    public TankManipulationBehaviour attachedTank;

    private int fallingDistance;

    // Rendering purposes only
    private FluidStack renderedFluid;

    public FaucetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        beltBehaviorOverride = false;

        fallingDistance = 0;
        renderedFluid = FluidStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived)
                .whileItemHeld(this::whenItemHeld);

        behaviours.add(attachedTank = new TankManipulationBehaviour(this, CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing()));
        behaviours.add(beltProcessing);
    }

    private BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual())
            return PASS;

        if (!GenericItemFilling.canItemBeFilled(level, transported.stack))
            return PASS;

        IFluidHandler fluidTank = attachedTank.getInventory();
        if (fluidTank == null)
            return PASS;

        if (fluidTank.getFluidInTank(0).isEmpty())
            return HOLD;

        if (GenericItemFilling.getRequiredAmountForItem(level, transported.stack, fluidTank.getFluidInTank(0)) == -1)
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

        IFluidHandler fluidTank = attachedTank.getInventory();
        if (fluidTank == null)
            return PASS;

        FluidStack fluid = fluidTank.getFluidInTank(0);
        if (fluidTank.getFluidInTank(0).isEmpty()) {
            updateRenderedFluid(FluidStack.EMPTY);
            return HOLD;
        }

        int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(getLevel(), transported.stack, fluid.copy());
        if (requiredAmountForItem == -1) {
            reset();
            return PASS;
        }

        if (requiredAmountForItem > fluid.getAmount())
            return HOLD;

        FluidStack drained = fluidTank.drain(TRANSFER_RATE * 5, FluidAction.EXECUTE);
        updateRenderedFluid(drained.copy());
        setFaucetOpen(true);
        updateFallDistance(2);

        if (!drained.isEmpty())
            transported.stack = GenericItemFilling.fillItem(level, drained.getAmount(), transported.stack, drained);
        return HOLD;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {

        int oldFallDist = fallingDistance;
        fallingDistance = compound.getInt("FallingDistance");
        if (oldFallDist != fallingDistance)
            invalidateRenderBoundingBox();

        renderedFluid = FluidStack.parseOptional(registries, compound.getCompound("RenderedFluid"));
        super.read(compound, registries, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("FallingDistance", fallingDistance);
        compound.put("RenderedFluid", renderedFluid.saveOptional(registries));
        super.write(compound, registries, clientPacket);
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

            IFluidHandler fluidTank = attachedTank.getInventory();
            if (fluidTank == null)
                return;

            for (boolean simulate : Iterate.trueAndFalse) {
                FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
                FluidStack drained = fluidTank.drain(TRANSFER_RATE, action);

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
        IFluidHandler fluidTank = attachedTank.getInventory();
        if (fluidTank == null)
            return false;

        return tryFill(fluidTank.drain(TRANSFER_RATE, action), action) > 0;
    }

    public void setFaucetOpen(boolean openState) {
        if (getBlockState().getValue(FaucetBlock.OPEN).equals(openState))
            return;

        BlockState state = getBlockState().setValue(FaucetBlock.OPEN, openState);
        getLevel().setBlockAndUpdate(getBlockPos(), state);
        FaucetBlock.playSound(null, getLevel(), getBlockPos(), openState);
    }


    public IFluidHandler getTargetTank() {
        Level level = getLevel();
        if (level == null)
            return null;

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
        updateFallDistance(fallDist);

        IFluidHandler targetTank = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, targetPos, Direction.UP.getOpposite());
        if (targetTank == null)
            return null;

        notifyUpdate();
        return targetTank;
    }

    private void spillOnEntities(FluidStack drained, FluidAction action) {
        if (getLevel() == null)
            return;

        List<Entity> entities = getLevel().getEntities(null, getFluidArea()); // Blacklist entities in the parameter

        if (drained.isEmpty())
            return;

        for (Entity entity : entities) {
//            if (entity instanceof LadleEntity ladleEntity) {
//                ItemStack ladle = ladleEntity.getBox();
//                IFluidHandlerItem fluidContainer = ladle.getCapability(Capabilities.FluidHandler.ITEM);
//                if (fluidContainer == null)
//                    continue;
//
//                if (fluidContainer.getFluidInTank(0).getAmount() < LADLE_CAPACITY) {
//                    FluidTank targetTank = fluidContainer.get;
//                    if (drained.isEmpty())
//                        break;
//
//                    int filled = targetTank.fill(drained.copy(), action);
//                    if (action.simulate() || filled <= 0)
//                        continue;
//
//                    drained.shrink(filled);
//                    LadleItem.setFluidContents(ladle, targetTank);
//                }
//            }

            if (action.execute()) {
                if (CMFluids.isHotFluid(drained.getFluid())) {
                    if (!entity.fireImmune()) {
                        entity.setRemainingFireTicks(MOLTEN_FLUID_BURNING_TIME);
                        if (entity.hurt(CMDamageTypes.moltenFluid(entity.level()), 4.0F))
                            entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
                    }
                }
            }
        }
    }

    protected int tryFill(FluidStack drained, FluidAction action) {
        IFluidHandler targetTank = getTargetTank();

        if (drained.isEmpty() || targetTank == null)
            return 0;

        return targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained.copy(), action)
                : targetTank.fill(drained.copy(), action);
    }

    protected void updateRenderedFluid(FluidStack drained) {
        if (!FluidStack.isSameFluidSameComponents(renderedFluid, drained)) {
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

    private AABB getFluidArea() {
        return new AABB(worldPosition)
                .expandTowards(0, -getFallingDistance(), 0);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return getFluidArea();
    }
}
