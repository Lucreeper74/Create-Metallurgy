package fr.lucreeper74.createmetallurgy.events;

import fr.lucreeper74.createmetallurgy.content.light_bulb.network.address.NetworkAddressRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CMClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (!isGameActive())
            return;

        NetworkAddressRenderer.tick();
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }
}
