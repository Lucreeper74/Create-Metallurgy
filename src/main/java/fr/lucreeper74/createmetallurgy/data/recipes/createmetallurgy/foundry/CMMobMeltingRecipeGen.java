package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMMobMeltingRecipeGen extends MobMeltingRecipeGen {

    public CMMobMeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            IRON_GOLEM = meltingEntity("iron_golem", EntityType.IRON_GOLEM, 6, CMFluids.MOLTEN_IRON, 135, 9),
            ZOMBIFIED_PIGLIN = meltingEntity("zombified_piglin", EntityType.ZOMBIFIED_PIGLIN, 4, CMFluids.MOLTEN_GOLD, 40, 6),
            PIGLIN = meltingEntity("piglin", EntityType.PIGLIN, 4, CMFluids.MOLTEN_GOLD, 10, 6),
            PIGLIN_BRUTE = meltingEntity("piglin_brute", EntityType.PIGLIN_BRUTE, 4, CMFluids.MOLTEN_GOLD, 20, 6),

    WITHER_SKELETON = create("wither_skeleton", b -> b
            .requireEntityType(EntityType.WITHER_SKELETON, 4)
            .requireMinHeat(9)
            .require(CMFluids.MOLTEN_IRON.get(), 270)
            .output(CMFluids.MOLTEN_STEEL.get(), 270));
}
