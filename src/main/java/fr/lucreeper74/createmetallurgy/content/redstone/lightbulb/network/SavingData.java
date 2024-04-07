package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network;

import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.MOD_ID;

public class SavingData {

    static Level levelCache;

    public static NetworkHandler create() {
        return NetworkHandler.HANDLER;
    }

    public static NetworkHandler loadFromDisk(CompoundTag nbt) {
        if(!NetworkHandler.HANDLER.networkList.isEmpty())
            return null;
        NetworkHandler data = create();
        Tag tag = nbt.get("Networks");
        data.deserializeNBT((ListTag) tag, levelCache);

        for (Network networks : data.networkList) {
            for (Network.Node nodes : networks.nodes.values()) {
                LightBulbBlockEntity light = (LightBulbBlockEntity) levelCache.getBlockEntity(nodes.pos);
                light.setNetwork(networks);
            }
        }
        return data;
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        levelCache = serverLevel;
        serverLevel.getDataStorage().computeIfAbsent(SavingData::loadFromDisk, SavingData::create, "networks");
    }
}
