package fr.lucreeper74.createmetallurgy.worldgen;

import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.worldgen.OreFeatureConfigEntry;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;

public class OresFeaturesRegistry {
    public static final OreFeatureConfigEntry WOLFRAMITE_ORE =
            create("wolfarmite_ore", 12, 8, -63, 70)
                    .standardDatagenExt()
                    .withNetherBlock(AllBlocks.WOLFRAMITE_ORE)
                    .biomeTag(BiomeTags.IS_NETHER)
                    .parent();

    private static OreFeatureConfigEntry create(String name, int clusterSize, float frequency,
                                                int minHeight, int maxHeight) {
        ResourceLocation id = Create.asResource(name);
        OreFeatureConfigEntry configDrivenFeatureEntry = new OreFeatureConfigEntry(id, clusterSize, frequency, minHeight, maxHeight);
        return configDrivenFeatureEntry;
    }

    public static void init() {}
}
