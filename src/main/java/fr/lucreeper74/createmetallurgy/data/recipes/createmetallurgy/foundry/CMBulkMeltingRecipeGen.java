package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMBulkMeltingRecipeGen extends BulkMeltingRecipeGen {

    public CMBulkMeltingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            /* Bulk Melting Recipes */
            ALL_METALS = allMetals();

    protected GeneratedRecipe allMetals() {
        for (CMMetals metal : CMMetals.values()) {
            metal(metal, CMMetals.ItemType.BLOCK); // Blocks
            metal(metal, CMMetals.ItemType.RAW_BLOCK); // Raw Blocks
        }
        return null;
    }
}