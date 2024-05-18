package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class NetworkHandler {

    final Map<Address, Network> networkList = new HashMap<>();

    public Network addNetwork(Level level, Address address) {
        Network network = new Network(level);
        networkList.put(address, network);
        return network;
    }

    public static class Address {
        public static final Address EMPTY = new Address(ItemStack.EMPTY);
        private static final Map<Item, Address> addresses = new IdentityHashMap<>();
        private ItemStack stack;
        private Item item;

        public static Address of(ItemStack stack) {
            if (stack.isEmpty())
                return EMPTY;
            if (!stack.hasTag())
                return addresses.computeIfAbsent(stack.getItem(), $ -> new Address(stack));
            return new Address(stack);
        }

        private Address(ItemStack stack) {
            this.stack = stack;
            item = stack.getItem();
        }

        public ItemStack getStack() {
            return stack;
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }
    }

    public Network getNetOf(Level level, INetworkNode actor) {
        Address key = actor.getAddress();
        if (!networkList.containsKey(key))
            addNetwork(level, key);
        return networkList.get(actor.getAddress());
    }
}