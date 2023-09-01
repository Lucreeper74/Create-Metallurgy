package fr.lucreeper74.createmetallurgy.content.castingbasin;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;

public class CastingBasinRecipe extends BasinRecipe {

        public CastingBasinRecipe(ProcessingRecipeParams params) {
            super(AllRecipeTypes.MELTING, params);
        }
    }