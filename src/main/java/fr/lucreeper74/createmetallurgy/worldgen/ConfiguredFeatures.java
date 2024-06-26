package fr.lucreeper74.createmetallurgy.worldgen;

import com.google.common.base.Suppliers;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.data.worldgen.features.OreFeatures.NETHERRACK;

public class ConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, CreateMetallurgy.MOD_ID);

    public static final Supplier<List<OreConfiguration.TargetBlockState>> WOLFRAMIE_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(NETHERRACK, CMBlocks.WOLFRAMITE_ORE.get().defaultBlockState())));

    public static final RegistryObject<ConfiguredFeature<?, ?>> WOLFRAMIE_ORE = CONFIGURED_FEATURES.register("wolframite_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(WOLFRAMIE_ORES.get(), 8))); //VeinSize


    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}