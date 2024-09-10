package fr.lucreeper74.createmetallurgy.content.foundry_mixer;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class AlloyingRecipe extends FoundryBasinRecipe {
        public AlloyingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
            super(CMRecipeTypes.ALLOYING, params);
        }
    }