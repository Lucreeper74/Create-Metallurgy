package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class FoundryRecipeGen<P extends FoundryRecipeParams, R extends FoundryRecipe<P>, B extends FoundryRecipeBuilder<P, R, B>> extends ProcessingRecipeGen<P, R, B> {

    public FoundryRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }
}
