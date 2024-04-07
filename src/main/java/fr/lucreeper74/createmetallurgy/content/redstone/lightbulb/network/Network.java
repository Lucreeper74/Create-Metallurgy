package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Network {

    public final Map<BlockPos, Node> nodes = new HashMap();
    final Level level;

    public Network(Level level) {
        this.level = level;
    }

    public void addNode(BlockPos pos) {
        nodes.put(pos, new Node(pos));
        NetworkHandler.HANDLER.setDirty();
    }

    public void removeNode(BlockPos pos) {
        nodes.remove(pos);
        if(nodes.isEmpty())
            NetworkHandler.HANDLER.networkList.remove(this);
        NetworkHandler.HANDLER.setDirty();
    }

    public class Node {

        BlockPos pos;

        public Node(BlockPos pos) {
            this.pos = pos;
        }

        public BlockPos getPos() { return pos;}

        @Override
        public int hashCode() {
            return pos.hashCode();
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("X", pos.getX());
            nbt.putInt("Y", pos.getY());
            nbt.putInt("Z", pos.getZ());
            return nbt;
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag nbtTagListNodes = new ListTag();
        for (Node node : nodes.values()) {
            nbtTagListNodes.add(node.serializeNBT());
        }
        nbt.put("Nodes", nbtTagListNodes);

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        ListTag nodes = (ListTag) nbt.get("Nodes");

        for (int i = 0; i < nodes.size(); i++) {
            Node node = deserializeNodeNBT(nodes.getCompound(i));
            this.nodes.put(node.pos, node);
        }
    }

    private Node deserializeNodeNBT(CompoundTag nbt) {
        return new Node(new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z")));
    }
}