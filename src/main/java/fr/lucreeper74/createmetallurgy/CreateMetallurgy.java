package fr.lucreeper74.createmetallurgy;

import com.simibubi.create.foundation.data.CreateRegistrate;
import fr.lucreeper74.createmetallurgy.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateMetallurgy.MOD_ID)
public class CreateMetallurgy {

    public static final String MOD_ID = "createmetallurgy";
    //private static final Logger LOGGER = LogUtils.getLogger();

    public static CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public CreateMetallurgy()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(eventBus);

        AllBlocks.register();
        AllFluids.register();
        AllItems.register();
        AllPModels.init();
        AllBlockEntityTypes.register();
        AllRecipeTypes.register(eventBus);


        eventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

    }

    public static ResourceLocation genRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
