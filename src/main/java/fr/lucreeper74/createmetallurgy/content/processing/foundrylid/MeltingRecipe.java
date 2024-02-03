package fr.lucreeper74.createmetallurgy.content.processing.foundrylid;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;

public class MeltingRecipe extends BasinRecipe {
    public MeltingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
            super(AllRecipeTypes.MELTING, params);
        }
}