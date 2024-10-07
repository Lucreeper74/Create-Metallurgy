package fr.lucreeper74.createmetallurgy;

import fr.lucreeper74.createmetallurgy.registries.CMPonderTags;
import fr.lucreeper74.createmetallurgy.registries.CMPonders;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateMetallurgyClient {

    public static void loadClient(IEventBus modEventBus) {
        modEventBus.addListener(CreateMetallurgyClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
//      CMPartialModels.init();
        CMPonderTags.register();
        CMPonders.register();
    }
}