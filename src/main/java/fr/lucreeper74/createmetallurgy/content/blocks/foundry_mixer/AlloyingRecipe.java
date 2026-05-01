package fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class AlloyingRecipe extends FoundryBasinRecipe {

    public AlloyingRecipe(ProcessingRecipeParams params) {
        super(CMRecipeTypes.ALLOYING, params);
    }
}