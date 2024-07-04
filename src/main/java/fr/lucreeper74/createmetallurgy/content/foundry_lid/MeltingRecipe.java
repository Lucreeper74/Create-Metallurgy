package fr.lucreeper74.createmetallurgy.content.foundry_lid;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class MeltingRecipe extends BasinRecipe {
    public MeltingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
            super(CMRecipeTypes.MELTING, params);
        }
}