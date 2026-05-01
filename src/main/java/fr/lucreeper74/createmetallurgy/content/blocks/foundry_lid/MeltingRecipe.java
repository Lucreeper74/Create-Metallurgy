package fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

public class MeltingRecipe extends FoundryBasinRecipe {
    public MeltingRecipe(ProcessingRecipeParams params) {
            super(CMRecipeTypes.MELTING, params);
        }
}