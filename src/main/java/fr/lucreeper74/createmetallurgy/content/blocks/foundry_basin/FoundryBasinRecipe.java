package fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

public class FoundryBasinRecipe extends BasinRecipe {

    protected FoundryBasinRecipe(IRecipeTypeInfo type, ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(type, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 3;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 4;
    }

}
