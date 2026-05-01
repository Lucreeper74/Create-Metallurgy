package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

public abstract class FoundryRecipeBuilder<P extends FoundryRecipeParams, R extends FoundryRecipe<P>, S extends FoundryRecipeBuilder<P, R, S>> extends ProcessingRecipeBuilder<P, R, S> {

    public FoundryRecipeBuilder(FoundryRecipe.Factory<P, R> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    protected abstract P createParams();

    public abstract S self();

    public S requireMinHeat(int minHeat) {
        params.minHeatRequirement = minHeat;
        return self();
    }

    public S requireMaxHeat(int maxHeat) {
        params.maxHeatRequirement = maxHeat;
        return self();
    }
}
