package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network;

import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.NetworkHandler.Address;
import net.minecraft.core.BlockPos;

public interface INetworkNode {
    int getTransmittedSignal();

    void setReceivedSignal(int power);

    boolean isAlive();

    BlockPos getLocation();

    Address getAddress();

}
