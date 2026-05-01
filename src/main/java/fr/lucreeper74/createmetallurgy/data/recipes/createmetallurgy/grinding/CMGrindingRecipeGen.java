package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.grinding;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMGrindingRecipeGen extends GrindingRecipeGen {

    public CMGrindingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            ALL_COPPER_BLOCKS = deoxidized(),
            ALL_WAXED_COPPER_BLOCKS = unwaxed()

            ;
}
