package fr.lucreeper74.createmetallurgy;

import fr.lucreeper74.createmetallurgy.ponders.CMPonders;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateMetallurgyClient {

    public static void loadClient(IEventBus modEventBus) {
        modEventBus.addListener(CreateMetallurgyClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        //CMPartialModels.init(); // Moved to main (Cause crash if here with ModernFix)
        PonderIndex.addPlugin(new CMPonders());
    }
}