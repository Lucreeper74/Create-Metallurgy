package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.Network;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbBlock.LEVEL;

public class LightBulbBlockEntity extends SmartBlockEntity {

    private Network network;
    private Boolean queueReady = false;
    private final List<BlockPos> sortedQueue = new ArrayList<>();
    private int pointer = 0;

    public LightBulbBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void buildQueue() {
        if (level == null || level.isClientSide)
            return;

        BlockPos pos = getBlockPos();
        SortedMap<Integer, BlockPos> queue = new TreeMap<>();

        for (Network.Node node : network.nodes.values()) {
            BlockPos nodePos = node.getPos();
            if (nodePos != pos)
                queue.put(getDistance(nodePos, pos), nodePos);
        }
        sortedQueue.addAll(queue.values());
        queue.clear();
        queueReady = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (AnimationTickHolder.getTicks() % 10 == 0 && queueReady) {
            if (pointer < sortedQueue.size()) {
                BlockPos nodePos = sortedQueue.get(pointer);
                if (level.getBlockEntity(nodePos).isRemoved())
                    return; //Continue if the current node is unloaded
                level.setBlock(nodePos, level.getBlockState(nodePos).setValue(LEVEL, getBlockState().getValue(LEVEL)), 2);
                pointer++;

            } else {
                queueReady = false;
                pointer = 0;
                sortedQueue.clear();
            }
        }
    }

    public Integer getDistance(BlockPos p1, BlockPos p2) {
        return (int) Mth.sqrt(
                Mth.square(p1.getX() - p2.getX()) +
                        Mth.square(p1.getY() - p2.getY()) +
                        Mth.square(p1.getZ() - p2.getZ()));
    }


    void searchNearLight(int range) {
        if (level == null || level.isClientSide)
            return;

        BlockPos pos = getBlockPos();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos foundPos = pos.offset(x, y, z);
                    if (level.getBlockEntity(foundPos) instanceof LightBulbBlockEntity foundLight && !foundPos.equals(pos)) {
                        Network foundNetwork = foundLight.getNetwork();
                        foundNetwork.addNode(pos);

                        if (network == null) { //First Network found -> Adding himself into it
                            network = foundNetwork;
                        } else if (foundNetwork != network) { //Found another network while already in one -> Merge the two
                            NetworkHandler.HANDLER.merge(network, foundNetwork);
                        }

                    }
                }
            }
        }

        if (network == null) { //No network found -> Create one
            network = NetworkHandler.HANDLER.create(level);
            network.addNode(pos);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide()) return;
        NetworkHandler.HANDLER.load(level);
    }
}
