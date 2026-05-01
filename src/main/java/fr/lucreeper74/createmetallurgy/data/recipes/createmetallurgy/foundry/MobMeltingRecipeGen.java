package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.MobMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.MobMeltingRecipeParams;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.concurrent.CompletableFuture;

public class MobMeltingRecipeGen extends FoundryRecipeGen<MobMeltingRecipeParams, MobMeltingRecipe, MobMeltingRecipe.Builder<MobMeltingRecipe>> {

    public MobMeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    /**
     * Recipes with input EntityType :
     *
     * @param recipeId   Recipe name / folders$
     * @param entityType EntityType input
     * @param damage     Damage deal to the input entity each burn
     * @param result     Fluid result
     * @param amount     Fluid amount
     * @param minHeat    Minimum Heat condition
     */
    protected GeneratedRecipe meltingEntity(String recipeId, EntityType<?> entityType, int damage, FluidEntry<BaseFlowingFluid.Flowing> result, int amount, int minHeat) {
        return create(recipeId, b -> b
                .requireEntityType(entityType, damage)
                .requireMinHeat(minHeat)
                .output(result.get(), amount));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CMRecipeTypes.ENTITY_MELTING;
    }

    @Override
    protected MobMeltingRecipe.Builder<MobMeltingRecipe> getBuilder(ResourceLocation id) {
        return new MobMeltingRecipe.Builder<>(MobMeltingRecipe::new, id);
    }
}
