package fr.lucreeper74.createmetallurgy;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.CreateNBTProcessors;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingWithSpout;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.NetworkHandler;
import fr.lucreeper74.createmetallurgy.data.CMDatagen;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.registries.*;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(CreateMetallurgy.MOD_ID)
public class CreateMetallurgy {

    public static final String MOD_ID = "createmetallurgy";

    public static final boolean HEATJS_LOADED = ModList.get().isLoaded("create_heat_js");
    public static final boolean KUBEJS_LOADED = ModList.get().isLoaded("kubejs");
    public static final boolean CREATEJS_LOADED = ModList.get().isLoaded("kubejs_create");

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();
    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
                .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    // HANDLERS
    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();

    public CreateMetallurgy(IEventBus eventBus, ModContainer container) {
        REGISTRATE.registerEventListeners(eventBus);

        CatnipServices.PLATFORM.executeOnClientOnly(() -> CMPartialModels::init); // Causing crash with ModernFix if Client init

        CMCreativeTabs.register(eventBus);
        CMDisplaySources.register();
        CMBlocks.register();
        CMItems.register();
        CMFluids.register();
        CMMenuTypes.register();
        CMEntityTypes.register();
        CMSpriteShifts.init();
        CMBlockEntityTypes.register();
        CMRecipeTypes.register(eventBus);
        CMParticleTypes.register(eventBus);
        CMDataComponents.register(eventBus);
        CMPackets.register();

        CMConfig.register(ModLoadingContext.get(), container);

        CMMetals.init(); // Init ALL_LOADED_METALS list

        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateMetallurgyClient.loadClient(eventBus));

        eventBus.addListener(CreateMetallurgy::init);
        eventBus.addListener(CreateMetallurgy::onRegister);
        eventBus.addListener(CMEntityTypes::registerEntityAttributes);
        eventBus.addListener(EventPriority.HIGHEST, CMDatagen::gatherDataHighPriority);
        eventBus.addListener(EventPriority.LOWEST, CMDatagen::gatherData);
    }

    public static void init(final FMLCommonSetupEvent event) {
        CMFluids.registerFluidInteractions();
        CreateNBTProcessors.register();

        event.enqueueWork(() -> {
            CastingWithSpout.registerDefaults();
        });
    }

    public static void onRegister(final RegisterEvent event) {
        CMArmInteract.init();
    }

    public static ResourceLocation asResource(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
