package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.BlockHelper;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.utils.LargeItemEmptying;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TundishBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public static final int PROCESSING_TIME = 10;

    FluidType lastFluidType;
    SmartFluidTankBehaviour internalTank;
    private int spreadCooldown;

    TransportedItemStack heldItem;
    protected int processingTicks;
    Map<Direction, TundishItemHandler> itemHandlers;

    public TundishBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        lastFluidType = FluidStack.EMPTY.getFluidType();

        itemHandlers = new IdentityHashMap<>();
        for (Direction d : Iterate.horizontalDirections) {
            itemHandlers.put(d, new TundishItemHandler(this, d));
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.TUNDISH.get(),
                (be, context) -> {
                    if (context != null && context.getAxis().isHorizontal())
                        return be.itemHandlers.get(context);
                    return null;
                }
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMBlockEntityTypes.TUNDISH.get(),
                (be, context) -> {
                    if (context != Direction.UP)
                        return be.internalTank.getCapability();
                    return null;
                }
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnels()
                .setInsertionHandler(this::tryInsertingFromSide));
        behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, CMConfig.server().tundishCapacity.get())
                .allowExtraction()
                .allowInsertion()
                .whenFluidUpdates(this::onFluidContentUpdate));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("spreadCooldown", spreadCooldown);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        spreadCooldown = compound.getInt("spreadCoolodwn");
        super.read(compound, registries, clientPacket);
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;
        ItemStack returned = ItemStack.EMPTY;

        if (!getHeldItemStack().isEmpty())
            return inserted;

        if (inserted.getCount() > 1 && GenericItemEmptying.canItemBeEmptied(level, inserted)) {
            returned = inserted.copyWithCount(inserted.getCount() - 1);
            inserted = inserted.copyWithCount(1);
        }

        if (simulate)
            return returned;

        transportedStack = transportedStack.copy();
        transportedStack.stack = inserted.copy();
        transportedStack.beltPosition = side.getAxis()
                .isVertical() ? .5f : 0;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        setHeldItem(transportedStack, side);
        setChanged();
        sendData();

        return returned;
    }

    public void onFluidContentUpdate() {
        FluidStack fluid = internalTank.getPrimaryHandler().getFluid();
        if (internalTank.isEmpty())
            return;

        if (spreadCooldown <= 0 || !lastFluidType.equals(fluid.getFluidType())) {
            spreadCooldown = fluid.getFluid().getTickDelay(getLevel());
            lastFluidType = fluid.getFluidType();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level != null && !level.isClientSide())
            handleFluidSpread();

        if (heldItem == null) {
            processingTicks = 0;
            return;
        }

        boolean onClient = level.isClientSide && !isVirtual();

        if (processingTicks > 0) {
            heldItem.prevBeltPosition = .5f;
            boolean wasAtBeginning = processingTicks == PROCESSING_TIME;
            if (!onClient || processingTicks < PROCESSING_TIME)
                processingTicks--;
            if (!continueProcessing()) {
                processingTicks = 0;
                notifyUpdate();
                return;
            }
            if (wasAtBeginning != (processingTicks == PROCESSING_TIME))
                sendData();
            return;
        }

        heldItem.prevBeltPosition = heldItem.beltPosition;
        heldItem.prevSideOffset = heldItem.sideOffset;

        heldItem.beltPosition += itemMovementPerTick();
        if (heldItem.beltPosition > 1) {
            heldItem.beltPosition = 1;

            if (onClient)
                return;

            Direction side = heldItem.insertedFrom;

            ItemStack tryExportingToBeltFunnel = getBehaviour(DirectBeltInputBehaviour.TYPE)
                    .tryExportingToBeltFunnel(heldItem.stack, side.getOpposite(), false);
            if (tryExportingToBeltFunnel != null) {
                if (tryExportingToBeltFunnel.getCount() != heldItem.stack.getCount()) {
                    if (tryExportingToBeltFunnel.isEmpty())
                        heldItem = null;
                    else
                        heldItem.stack = tryExportingToBeltFunnel;
                    notifyUpdate();
                    return;
                }
                if (!tryExportingToBeltFunnel.isEmpty())
                    return;
            }

            BlockPos nextPosition = worldPosition.relative(side);
            DirectBeltInputBehaviour directBeltInputBehaviour =
                    BlockEntityBehaviour.get(level, nextPosition, DirectBeltInputBehaviour.TYPE);
            if (directBeltInputBehaviour == null) {
                if (!BlockHelper.hasBlockSolidSide(level.getBlockState(nextPosition), level, nextPosition,
                        side.getOpposite())) {
                    ItemStack ejected = heldItem.stack;
                    Vec3 outPos = VecHelper.getCenterOf(worldPosition)
                            .add(Vec3.atLowerCornerOf(side.getNormal())
                                    .scale(.75));
                    float movementSpeed = itemMovementPerTick();
                    Vec3 outMotion = Vec3.atLowerCornerOf(side.getNormal())
                            .scale(movementSpeed)
                            .add(0, 1 / 8f, 0);
                    outPos.add(outMotion.normalize());
                    ItemEntity entity = new ItemEntity(level, outPos.x, outPos.y + 6 / 16f, outPos.z, ejected);
                    entity.setDeltaMovement(outMotion);
                    entity.setDefaultPickUpDelay();
                    entity.hurtMarked = true;
                    level.addFreshEntity(entity);

                    heldItem = null;
                    notifyUpdate();
                }
                return;
            }

            if (!directBeltInputBehaviour.canInsertFromSide(side))
                return;

            ItemStack returned = directBeltInputBehaviour.handleInsertion(heldItem.copy(), side, false);

            if (returned.isEmpty()) {
                heldItem = null;
                notifyUpdate();
                return;
            }

            if (returned.getCount() != heldItem.stack.getCount()) {
                heldItem.stack = returned;
                notifyUpdate();
                return;
            }

            return;
        }

        if (heldItem.prevBeltPosition < .5f && heldItem.beltPosition >= .5f) {
            if (!GenericItemEmptying.canItemBeEmptied(level, heldItem.stack))
                return;
            heldItem.beltPosition = .5f;
            if (onClient)
                return;
            processingTicks = PROCESSING_TIME;
            sendData();
        }
    }

    protected boolean continueProcessing() {
        if (level.isClientSide && !isVirtual())
            return true;
        if (processingTicks < 5)
            return true;
        if (!GenericItemEmptying.canItemBeEmptied(level, heldItem.stack))
            return false;

        int drainAmount = internalTank.getPrimaryHandler().getTankCapacity(0);

        Pair<FluidStack, ItemStack> emptyItem = LargeItemEmptying.emptyItem(level, heldItem.stack, drainAmount, true);
        FluidStack fluidFromItem = emptyItem.getFirst();

        if (processingTicks > 5) {
            if (internalTank.getPrimaryHandler()
                    .fill(fluidFromItem, FluidAction.SIMULATE) != fluidFromItem.getAmount()) {
                processingTicks = PROCESSING_TIME;
                return true;
            }
            return true;
        }

        emptyItem = LargeItemEmptying.emptyItem(level, heldItem.stack.copy(), drainAmount, false);

        // Process finished
        ItemStack out = emptyItem.getSecond();
        if (!out.isEmpty())
            heldItem.stack = out;
        else
            heldItem = null;
        internalTank.getPrimaryHandler()
                .fill(fluidFromItem, FluidAction.EXECUTE);
        notifyUpdate();
        return true;
    }

    private float itemMovementPerTick() {
        return 1 / 8f;
    }

    private void handleFluidSpread() {
        if (internalTank.isEmpty())
            return;

        if (spreadCooldown > 0) {
            spreadCooldown--;
            return;
        }

        boolean front = getBlockState().getValue(TundishBlock.FRONT);
        boolean rear = getBlockState().getValue(TundishBlock.REAR);
        Direction.Axis axis = getBlockState().getValue(TundishBlock.AXIS);

        if (front)
            trySpreadFluid(Direction.get(Direction.AxisDirection.POSITIVE, axis));
        if (rear)
            trySpreadFluid(Direction.get(Direction.AxisDirection.NEGATIVE, axis));

        FluidStack fluid = internalTank.getPrimaryHandler().getFluid();
        spreadCooldown = fluid.getFluid().getTickDelay(getLevel());
    }

    private void trySpreadFluid(Direction direction) {
        TundishBlockEntity neighborBE = getNeighborTundish(direction);
        if (neighborBE == null)
            return;

        SmartFluidTank neighborTank = neighborBE.internalTank.getPrimaryHandler();

        int current = internalTank.getPrimaryHandler().getFluidAmount();
        int neighbor = neighborTank.getFluidAmount();

        if (current <= neighbor)
            return;

        int transfer = (current - neighbor) / 2;

        if (transfer <= 0)
            return;

        FluidStack drained = internalTank.getPrimaryHandler().drain(transfer, FluidAction.SIMULATE);
        int filled = neighborTank.fill(drained, FluidAction.SIMULATE);
        if (filled > 0) {
            drained = internalTank.getPrimaryHandler().drain(filled, FluidAction.EXECUTE);
            neighborTank.fill(drained, FluidAction.EXECUTE);
        }
    }

    public void randomTick() {
        // Not overriding TundishBlock.handlePrecipitation()
        // because it's too slow for smooth water collection
        if (getLevel().isRaining())
            handlePrecipitation();

        handleStalactiteDrip();
    }

    protected void handlePrecipitation() {
        BlockPos heightMapPos = getLevel().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, getBlockPos());
        BlockPos surfacePos = heightMapPos.below();
        if (!surfacePos.equals(getBlockPos()))
            return; // rain blocked

        Biome.Precipitation precipitation = getLevel().getBiome(getBlockPos()).value().getPrecipitationAt(getBlockPos());
        if (precipitation == Biome.Precipitation.RAIN && getLevel().getRandom().nextFloat() < .6f)
            internalTank.getPrimaryHandler().fill(new FluidStack(Fluids.WATER,
                    CMConfig.server().tundishPrecipitationAmount.get()), FluidAction.EXECUTE);
    }

    public void handleStalactiteDrip() {
        BlockPos dripTip = PointedDripstoneBlock.findStalactiteTipAboveCauldron(getLevel(), getBlockPos());
        if (dripTip == null)
            return;

        Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType((ServerLevel) getLevel(), dripTip);
        if (fluid != Fluids.EMPTY && fluid instanceof FlowingFluid flowing) {

            if (!fluid.isSource(flowing.getSource(false)))
                return;

            FluidType fluidType = fluid.getFluidType();
            FluidType.DripstoneDripInfo dripInfo = fluidType.getDripInfo();
            SoundEvent dripSound = fluidType.getSound(null, getLevel(), getBlockPos(), SoundActions.CAULDRON_DRIP);

            if (dripInfo == null)
                return;

            for (int i = 0; i < 5; i++) {
                if (getLevel().getRandom().nextFloat() < dripInfo.chance() * 3) {
                    level.playSound(null, getBlockPos(), dripSound, SoundSource.BLOCKS, 2f, level.getRandom().nextFloat() * .1f + .9f);
                    internalTank.getPrimaryHandler().fill(new FluidStack(fluid, CMConfig.server().tundishDripAmount.get()),
                            FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    public ItemStack getHeldItemStack() {
        return heldItem == null ? ItemStack.EMPTY : heldItem.stack;
    }

    public void setHeldItem(TransportedItemStack heldItem, Direction insertedFrom) {
        this.heldItem = heldItem;
        this.heldItem.insertedFrom = insertedFrom;
    }

    private TundishBlockEntity getNeighborTundish(Direction direction) {
        BlockPos frontPos = getBlockPos().relative(direction);
        if (!getLevel().getBlockState(frontPos).is(CMBlocks.TUNDISH_BLOCK))
            return null;
        return (TundishBlockEntity) getLevel().getBlockEntity(frontPos);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition, null));
    }
}
