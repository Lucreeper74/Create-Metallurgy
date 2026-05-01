package fr.lucreeper74.createmetallurgy.worldgen;

import com.simibubi.create.infrastructure.worldgen.ConfigPlacementFilter;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

public class CMPlacedFeatures {
    public static final ResourceKey<PlacedFeature> WOLFRAMIE_ORE = registerKey("wolframite_ore");

    public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> featureLookup = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> wolframiteOre = featureLookup.getOrThrow(CMConfiguredFeatures.WOLFRAMIE_ORE_KEY);

        register(ctx, WOLFRAMIE_ORE, wolframiteOre, placement(CountPlacement.of(7), 0, 60));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CreateMetallurgy.asResource(name));
    }

    private static List<PlacementModifier> placement(PlacementModifier frequency, int minHeight, int maxHeight) {
        return List.of(
                frequency,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(minHeight), VerticalAnchor.absolute(maxHeight)),
                ConfigPlacementFilter.INSTANCE
        );
    }
}
