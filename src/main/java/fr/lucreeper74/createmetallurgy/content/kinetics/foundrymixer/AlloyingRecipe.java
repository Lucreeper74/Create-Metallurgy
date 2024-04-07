package fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class AlloyingRecipe extends BasinRecipe {
        public AlloyingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
            super(CMRecipeTypes.ALLOYING, params);
        }
    }