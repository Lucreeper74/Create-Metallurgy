package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MillingRecipeGen;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMMillingRecipeGen extends MillingRecipeGen {

    public CMMillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            CRUSHED_RAW_WOLFRAMITE = create(CMItems.CRUSHED_RAW_WOLFRAMITE::get, b -> b.duration(150)
            .output(CMItems.DIRTY_WOLFRAMITE_DUST.get())
            .output(.25f, CMItems.DIRTY_WOLFRAMITE_DUST.get())),

    CRUSHED_RAW_COPPER = create(AllItems.CRUSHED_COPPER::get, b -> b.duration(150)
            .output(CMItems.DIRTY_COPPER_DUST.get())
            .output(.25f, CMItems.DIRTY_COPPER_DUST.get())),

    CRUSHED_RAW_GOLD = create(AllItems.CRUSHED_GOLD::get, b -> b.duration(150)
            .output(CMItems.DIRTY_GOLD_DUST.get())
            .output(.25f, CMItems.DIRTY_GOLD_DUST.get())),

    CRUSHED_RAW_IRON = create(AllItems.CRUSHED_IRON::get, b -> b.duration(150)
            .output(CMItems.DIRTY_IRON_DUST.get())
            .output(.25f, CMItems.DIRTY_IRON_DUST.get())),

    CRUSHED_RAW_ZINC = create(AllItems.CRUSHED_ZINC::get, b -> b.duration(150)
            .output(CMItems.DIRTY_ZINC_DUST.get())
            .output(.25f, CMItems.DIRTY_ZINC_DUST.get()));
}
