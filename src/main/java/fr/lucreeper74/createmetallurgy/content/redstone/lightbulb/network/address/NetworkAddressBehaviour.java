package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.address;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.INetworkNode;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.NetworkHandler.Address;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class NetworkAddressBehaviour extends BlockEntityBehaviour implements INetworkNode, ClipboardCloneable {

    public static final BehaviourType<NetworkAddressBehaviour> TYPE = new BehaviourType<>();

    ValueBoxTransform slot;
    Address address;
    private IntSupplier transmission;
    private IntConsumer signalCallback;


    public NetworkAddressBehaviour(SmartBlockEntity be, ValueBoxTransform AddressSlot) {
        super(be);
        address = Address.EMPTY;
        slot = AddressSlot;
    }

    public static NetworkAddressBehaviour networkNode(SmartBlockEntity be, ValueBoxTransform slot,
                                         IntConsumer signalCallback, IntSupplier transmission) {
        NetworkAddressBehaviour behaviour = new NetworkAddressBehaviour(be, slot);
        behaviour.signalCallback = signalCallback;
        behaviour.transmission = transmission;
        return behaviour;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private NetworkHandler getHandler() {
        return CreateMetallurgy.NETWORK_HANDLER;
    }

    public void setAddress(ItemStack stack) {
        stack = stack.copy();
        stack.setCount(1);
        boolean changed = !ItemStack.isSameItem(stack, address.getStack());

        if (changed) {
            getHandler().getNetOf(getWorld(), this).removeNode(this);
        } else return;

        address = Address.of(stack);
        blockEntity.sendData();

        getHandler().getNetOf(getWorld(), this).addNode(this);
    }

    @Override
    public void unload() {
        super.unload();
        if (getWorld().isClientSide)
            return;
        getHandler().getNetOf(getWorld(), this).removeNode(this);
    }


    public void notifySignalChange() {
        CreateMetallurgy.NETWORK_HANDLER.getNetOf(getWorld(), this).transmit(this);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;
        getHandler().getNetOf(getWorld(), this).addNode(this);
    }

    public boolean testHit(Vec3 hit) {
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
        return slot.testHit(state, localHit);
    }

    @Override
    public boolean isAlive() {
        Level level = getWorld();
        BlockPos pos = getPos();
        if (blockEntity.isChunkUnloaded())
            return false;
        if (blockEntity.isRemoved())
            return false;
        if (!level.isLoaded(pos))
            return false;
        return level.getBlockEntity(pos) == blockEntity;
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.put("Address", address.getStack()
                .save(new CompoundTag()));
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        address = Address.of(ItemStack.of(nbt.getCompound("Address")));
    }

    @Override
    public int getTransmittedSignal() {
        return transmission.getAsInt();
    }

    @Override
    public void setReceivedSignal(int power) {
        signalCallback.accept(power);
    }

    @Override
    public BlockPos getLocation() {
        return getPos();
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public String getClipboardKey() {
        return "Address";
    }

    @Override
    public boolean writeToClipboard(CompoundTag tag, Direction side) {
        tag.put("AddressClip", address.getStack().save(new CompoundTag()));
        return true;
    }

    @Override
    public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("AddressClip"))
            return false;
        if (simulate)
            return true;
        setAddress(ItemStack.of(tag.getCompound("AddressClip")));
        return true;
    }
}
