package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.DataGenerator;

@SuppressWarnings("unused")
public class CMPressingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            GRAPHITE = create(CMItems.GRAPHITE::get, b -> b.output(CMItems.GRAPHITE_BLANK_MOLD.get()));

    //

    public CMPressingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}
