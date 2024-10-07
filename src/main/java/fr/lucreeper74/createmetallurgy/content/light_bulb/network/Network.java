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
}