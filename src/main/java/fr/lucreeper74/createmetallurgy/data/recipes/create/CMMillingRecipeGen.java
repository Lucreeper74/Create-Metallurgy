package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.PackOutput;

@SuppressWarnings("unused")
public class CMMillingRecipeGen extends CMProcessingRecipesGen {

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

    //

    public CMMillingRecipeGen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MILLING;
    }
}
