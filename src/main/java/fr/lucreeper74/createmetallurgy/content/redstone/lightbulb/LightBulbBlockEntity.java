package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.address.NetworkAddressBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class LightBulbBlockEntity extends SmartBlockEntity {

    private boolean receivedSignalChanged;
    private NetworkAddressBehaviour addressBehaviour;
    private int receivedSignal;
    private int transmittedSignal;

    public LightBulbBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
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
        if (receivedSignal != blockState.getValue(LightBulbBlock.LEVEL)) {
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

    //    @Override
//    public void onLoad() {
//        super.onLoad();
//        if (level == null || level.isClientSide()) return;
//        NetworkHandler.HANDLER.load(level);
//    }
}
