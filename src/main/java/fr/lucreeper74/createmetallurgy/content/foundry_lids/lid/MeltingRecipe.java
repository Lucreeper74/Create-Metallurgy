package fr.lucreeper74.createmetallurgy.content.foundry_lids.lid;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class MeltingRecipe extends FoundryBasinRecipe {
    public MeltingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
            super(CMRecipeTypes.MELTING, params);
        }
}