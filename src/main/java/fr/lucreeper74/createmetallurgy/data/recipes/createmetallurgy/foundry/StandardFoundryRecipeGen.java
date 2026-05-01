package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.StandardFoundryRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public abstract class StandardFoundryRecipeGen<R extends StandardFoundryRecipe> extends FoundryRecipeGen<FoundryRecipeParams, R, StandardFoundryRecipe.Builder<R>>  {

    public StandardFoundryRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected StandardFoundryRecipe.Serializer<R> getSerializer() {
        return getRecipeType().getSerializer();
    }

    @Override
    protected StandardFoundryRecipe.Builder<R> getBuilder(ResourceLocation id) {
        return new StandardFoundryRecipe.Builder<>(getSerializer().factory(), id);
    }
}
