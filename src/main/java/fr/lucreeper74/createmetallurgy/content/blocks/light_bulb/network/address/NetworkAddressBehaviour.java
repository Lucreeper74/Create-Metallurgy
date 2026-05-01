package fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.address;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.INetworkNode;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.NetworkHandler;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.NetworkHandler.Address;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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
        return slot.testHit(getWorld(), getPos(), state, localHit);
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
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("Address", address.getStack()
                .saveOptional(registries));
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        address = Address.of(ItemStack.parseOptional(registries, nbt.getCompound("Address")));
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
    public boolean writeToClipboard(HolderLookup.@NotNull Provider registries, CompoundTag tag, Direction side) {
        tag.put("AddressClip", address.getStack().saveOptional(registries));
        return true;
    }

    @Override
    public boolean readFromClipboard(HolderLookup.@NotNull Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("AddressClip"))
            return false;
        if (simulate)
            return true;
        setAddress(ItemStack.parseOptional(registries, tag.getCompound("AddressClip")));
        return true;
    }
}
