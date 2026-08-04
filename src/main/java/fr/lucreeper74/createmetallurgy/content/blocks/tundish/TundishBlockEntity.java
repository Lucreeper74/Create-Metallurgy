package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class TundishBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    TransportedItemStack heldItem;
    FluidType lastFluidType;
    SmartFluidTankBehaviour internalTank;
    private int spreadCooldown;

    public TundishBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        lastFluidType = FluidStack.EMPTY.getFluidType();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
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
        behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, CMConfig.server().tundishCapacity.get())
                .allowExtraction()
                .allowInsertion()
                .whenFluidUpdates(this::onFluidUpdate));
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

    public void onFluidUpdate() {
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
        if (getLevel() == null)
            return;

        if (getLevel().isClientSide())
            return;

        handleFluidSpread();
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
