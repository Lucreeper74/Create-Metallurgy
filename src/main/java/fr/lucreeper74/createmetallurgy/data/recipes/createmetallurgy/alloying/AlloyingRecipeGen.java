package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.alloying;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.AlloyingRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.concurrent.CompletableFuture;

public class AlloyingRecipeGen extends StandardProcessingRecipeGen<AlloyingRecipe> {

    public AlloyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    /**
     * Recipe for basic Alloys from 2 Fluids :
     *
     * @param recipeId     Recipe name / folders
     * @param fluid1       Input first
     * @param amount1      First amount
     * @param fluid2       Input second
     * @param amount2      Second amount
     * @param result       Result fluid
     * @param amountResult Result amount
     * @param duration     Processing time
     */
    protected GeneratedRecipe basicAlloy(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid1, int amount1, FluidEntry<BaseFlowingFluid.Flowing> fluid2, int amount2, FluidEntry<BaseFlowingFluid.Flowing> result, int amountResult, HeatCondition heatCondition, int duration) {
        return create(recipeId, b -> b.duration(duration)
                .require(fluid1.get(), amount1)
                .require(fluid2.get(), amount2)
                .requiresHeat(heatCondition)
                .output(result.get(), amountResult));
    }

    /**
     * Recipe for basic Alloys from Fluid + Item (Tag) :
     *
     * @param recipeId     Recipe name / folders
     * @param fluid        Input first
     * @param amount       First amount
     * @param itemTag      Input tag
     * @param result       Result fluid
     * @param amountResult Result amount
     * @param duration     Processing time
     */
    protected GeneratedRecipe basicAlloy(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> itemTag, FluidEntry<BaseFlowingFluid.Flowing> result, int amountResult, HeatCondition heatCondition, int duration) {
        return create(recipeId, b -> b.duration(duration)
                .require(fluid.get(), amount)
                .require(itemTag)
                .requiresHeat(heatCondition)
                .output(result.get(), amountResult));
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.ALLOYING;
    }
}