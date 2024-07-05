package fr.lucreeper74.createmetallurgy.content.light_bulb.network;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.world.level.Level;

import java.util.*;

public class Network {

    public final Set<INetworkNode> nodes = new HashSet<>();
    final Level level;

    public Network(Level level) {
        this.level = level;
    }

    public void addNode(INetworkNode actor) {
        nodes.add(actor);
    }

    public void removeNode(INetworkNode actor) {
        nodes.remove(actor);
        if (nodes.isEmpty())
            CreateMetallurgy.NETWORK_HANDLER.networkList.remove(actor.getAddress());
    }

    public void transmit(INetworkNode actor) {
        int power = 0;

        for (INetworkNode other : nodes) {
            if (!other.isAlive()) {
                continue;
            }

//            if (!withinRange(actor, other))
//                continue;

            if (power < 15)
                power = Math.max(other.getTransmittedSignal(), power);
        }


        for (INetworkNode node : nodes) {
            node.setReceivedSignal(power);
        }
    }










//    public CompoundTag serializeNBT() {
//        CompoundTag nbt = new CompoundTag();
//
//        ListTag nbtTagListNodes = new ListTag();
//        for (Node node : nodes.values()) {
//            nbtTagListNodes.add(node.serializeNBT());
//        }
//        nbt.put("Nodes", nbtTagListNodes);
//
//        return nbt;
//    }
//
//    public void deserializeNBT(CompoundTag nbt) {
//        ListTag nodes = (ListTag) nbt.get("Nodes");
//
//        for (int i = 0; i < nodes.size(); i++) {
//            Node node = deserializeNodeNBT(nodes.getCompound(i));
//            this.nodes.put(node.pos, node);
//        }
//    }
//
//    private Node deserializeNodeNBT(CompoundTag nbt) {
//        return new Node(new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z")));
//    }
}