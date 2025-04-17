package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

@SuppressWarnings("unused")
public class CMPressingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            GRAPHITE = create(CMItems.GRAPHITE::get, b -> b.output(CMItems.GRAPHITE_BLANK_MOLD.get()));

    //

    public CMPressingRecipeGen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}