package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMPressingRecipeGen extends PressingRecipeGen {

    public CMPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            GRAPHITE = create(CMItems.GRAPHITE::get, b -> b.output(CMItems.GRAPHITE_BLANK_MOLD.get()));
}