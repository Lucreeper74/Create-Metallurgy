package fr.lucreeper74.createmetallurgy.events;

import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.address.NetworkAddressRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class CMClientEvents {

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (!isGameActive())
            return;

        NetworkAddressRenderer.tick();
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }
}
