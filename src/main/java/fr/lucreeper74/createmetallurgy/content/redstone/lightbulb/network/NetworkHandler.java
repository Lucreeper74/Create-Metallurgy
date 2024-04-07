package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network;

import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler extends SavedData {

    public static final NetworkHandler HANDLER = new NetworkHandler();

    final List<Network> networkList = new ArrayList<>();

    public Network create(Level level) {
        Network newNetwork = new Network(level);
        networkList.add(newNetwork);
        setDirty();
        return newNetwork;
    }

    public void merge(Network a, Network b) {
        a.nodes.putAll(b.nodes);
        b.nodes.clear();
        networkList.remove(b);
        setDirty();
    }

    public void load(Level level) {
        if(HANDLER.networkList.isEmpty())
            return;
        for (Network networks : HANDLER.networkList) {
            for (Network.Node node : networks.nodes.values()) {
                LightBulbBlockEntity light = (LightBulbBlockEntity) level.getBlockEntity(node.pos);
                light.setNetwork(networks);
            }
        }
    }

    public ListTag serializeNBT() {
        ListTag tagList = new ListTag();
        for (Network network : networkList) {
            tagList.add(network.serializeNBT());
        }
        return tagList;
    }

    public void deserializeNBT(ListTag nbt, Level level) {
        for (int i = 0; i < nbt.size(); i++) {
            Network network = new Network(level);
            network.deserializeNBT(nbt.getCompound(i));
            networkList.add(network);
        }
    }

    @Override
    @NotNull
    public CompoundTag save(CompoundTag tag) {
        tag.put("Networks", serializeNBT());
        return tag;
    }
}