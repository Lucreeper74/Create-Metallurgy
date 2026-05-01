package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.concurrent.CompletableFuture;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams.DEFAULT_MAX_HEAT;
import static fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider.HEAT_CONDITION_THRESHOLD;

public class BulkMeltingRecipeGen extends StandardFoundryRecipeGen<BulkMeltingRecipe> {

    public BulkMeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    /**
     * Recipe heat condition for metal based on metal fluid temperature
     */
    protected int getMetalHeat(CMMetals metal) {
        return (int) (((float) DEFAULT_MAX_HEAT / (HEAT_CONDITION_THRESHOLD * 5)) * metal.getMeltingPoint());
    }

    /**
     * Recipes with CMMetals :
     *
     * @param metal    metal
     * @param itemType metal item Type
     */
    protected GeneratedRecipe metal(CMMetals metal, CMMetals.ItemType itemType) {
        TagKey<Item> inputTag = metal.getItemTag(itemType);

        return create(metal.getName() + "/" + itemType.getName(),b -> {
            b.requireMinHeat(getMetalHeat(metal))
                    .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                    .require(inputTag)
                    .duration((int) (CMRecipeProvider.MELTING_DURATION * itemType.getDurationFactor() * .7f))
                    .output(metal.getFluid().get(), itemType.getFluidAmount());

            if (itemType.isImpure())
                b.output(CMFluids.MOLTEN_SLAG.get(), itemType.getImpurity());

            return b;
        });
    }

    /**
     * Recipes with input Tag :
     *
     * @param recipeId Recipe name / folders
     * @param inputTag Item tag input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingTag(String recipeId, TagKey<Item> inputTag, FluidEntry<BaseFlowingFluid.Flowing> result, int amount, int minHeat, int duration) {
        return create(recipeId, b -> b
                .requireMinHeat(minHeat)
                .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                .require(inputTag)
                .duration(duration)
                .output(result.get(), amount));
    }

    /**
     * Recipes with input Items :
     *
     * @param recipeId Recipe name / folders
     * @param input    Item input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingItem(String recipeId, ItemLike input, FluidEntry<BaseFlowingFluid.Flowing> result, int amount, int minHeat, int duration) {
        return create(recipeId, b -> b
                .requireMinHeat(minHeat)
                .require(input)
                .duration(duration)
                .output(result.get(), amount));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CMRecipeTypes.BULK_MELTING;
    }
}