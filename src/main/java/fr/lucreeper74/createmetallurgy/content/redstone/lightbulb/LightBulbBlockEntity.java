package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.ResetableLazy;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.address.NetworkAddressBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class LightBulbBlockEntity extends SmartBlockEntity {

    private boolean receivedSignalChanged;
    private NetworkAddressBehaviour addressBehaviour;
    private int receivedSignal;
    private int transmittedSignal;
    ResetableLazy<DyeColor> colorProvider;

    public LerpedFloat glow = LerpedFloat.linear();

    public LightBulbBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        colorProvider = ResetableLazy.of(() -> {
            BlockState blockState = getBlockState();
            if (blockState.getBlock() instanceof LightBulbBlock)
                return ((LightBulbBlock) blockState.getBlock()).getColor();
            return DyeColor.WHITE;
        });
    }

    public DyeColor getColor() {
        return colorProvider.get();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
        addressBehaviour = createAddressSlot();
        behaviours.add(addressBehaviour);
    }

    protected NetworkAddressBehaviour createAddressSlot() {
        return NetworkAddressBehaviour.networkNode(this, new LightBulbAddressSlot(), this::setSignal, this::getSignal);
    }

    public int getSignal() {
        return transmittedSignal;
    }

    public void setSignal(int strength) {
        if (receivedSignal != strength)
            receivedSignalChanged = true;
        receivedSignal = strength;
    }

    public void transmit(int strength) {
        transmittedSignal = strength;
        glow.chase((double) strength / 15, .5f, LerpedFloat.Chaser.EXP);
        if (addressBehaviour != null)
            addressBehaviour.notifySignalChange();
    }

    @Override
    public void initialize() {
        if (addressBehaviour == null) {
            addressBehaviour = createAddressSlot();
            attachBehaviourLate(addressBehaviour);
        }
        super.initialize();
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Receive", getReceivedSignal());
        compound.putBoolean("ReceivedChanged", receivedSignalChanged);
        compound.putInt("Transmit", transmittedSignal);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        receivedSignal = compound.getInt("Receive");
        receivedSignalChanged = compound.getBoolean("ReceivedChanged");
        if (level == null || level.isClientSide)
            transmittedSignal = compound.getInt("Transmit");
    }

    @Override
    public void tick() {
        super.tick();

        BlockState blockState = getBlockState();
        int lightLevel = blockState.getValue(LightBulbBlock.LEVEL);

        if(level.isClientSide) {
            glow.tickChaser();
            glow.chase(lightLevel, .2f, LerpedFloat.Chaser.EXP);
        }

        if (receivedSignal != lightLevel) {
            receivedSignalChanged = true;
            level.setBlockAndUpdate(worldPosition, blockState.setValue(LightBulbBlock.LEVEL, receivedSignal));
        }

        if (receivedSignalChanged) {
            level.blockUpdated(getBlockPos(), blockState.getBlock());
            receivedSignalChanged = false;
        }
    }

    public int getReceivedSignal() {
        return receivedSignal;
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        colorProvider.reset();
    }
}
