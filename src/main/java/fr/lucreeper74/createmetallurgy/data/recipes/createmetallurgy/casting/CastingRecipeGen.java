package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.casting;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class CastingRecipeGen extends BaseRecipeProvider {

    public CastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    /**
     * Recipe with result as recipe name
     */
    protected GeneratedRecipe create(CMRecipeTypes type, ItemLike result, UnaryOperator<CastingRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.asResource(RegisteredObjectsHelper.getKeyOrThrow(result.asItem()).getPath())))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    /**
     * Recipe with recipe name provided by the function
     */
    protected GeneratedRecipe create(CMRecipeTypes type, String name, UnaryOperator<CastingRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.asResource(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return modid + "'s Casting Recipes";
    }

    /**
     * Recipes with output Tags :
     *
     * @param recipeId  Recipe name / folders
     * @param mold      Mold used (Optional)
     * @param fluid     Input
     * @param amount    Fluid amount
     * @param resultTag Output from tag
     * @param duration  Processing time
     */
    protected GeneratedRecipe tableTag(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTagWithMold(CMRecipeTypes.CASTING_IN_TABLE, recipeId, mold, moldConsumed, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe tableTag(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTag(CMRecipeTypes.CASTING_IN_TABLE, recipeId, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe basinTag(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTagWithMold(CMRecipeTypes.CASTING_IN_BASIN, recipeId, mold, moldConsumed, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe basinTag(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTag(CMRecipeTypes.CASTING_IN_BASIN, recipeId, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe castingTagWithMold(CMRecipeTypes recipeType, String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        ResourceLocation location = resultTag.location();
        create(recipeType, recipeId, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(mold)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(resultTag));

        return null;
    }

    protected GeneratedRecipe castingTag(CMRecipeTypes recipeType, String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        ResourceLocation location = resultTag.location();
        create(recipeType, recipeId, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(fluid.get(), amount)
                .output(resultTag));

        return null;
    }


    /**
     * Recipes with output Items :
     *
     * @param mold     Mold used (Optional)
     * @param fluid    Input
     * @param amount   Fluid amount
     * @param result   Output from Item
     * @param duration Processing time
     */
    protected GeneratedRecipe table(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return castingWithMold(CMRecipeTypes.CASTING_IN_TABLE, recipeId, mold, moldConsumed, fluid, amount, result, duration);
    }

    protected GeneratedRecipe table(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return casting(CMRecipeTypes.CASTING_IN_TABLE, recipeId, fluid, amount, result, duration);
    }

    protected GeneratedRecipe basin(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return castingWithMold(CMRecipeTypes.CASTING_IN_BASIN, recipeId, mold, moldConsumed, fluid, amount, result, duration);
    }

    protected GeneratedRecipe basin(String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return casting(CMRecipeTypes.CASTING_IN_BASIN, recipeId, fluid, amount, result, duration);
    }

    protected GeneratedRecipe castingWithMold(CMRecipeTypes recipeType, String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        create(recipeType, recipeId, b -> b.duration(duration)
                .require(mold)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }

    protected GeneratedRecipe casting(CMRecipeTypes recipeType, String recipeId, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        create(recipeType, recipeId, b -> b.duration(duration)
                .require(fluid.get(), amount)
                .output(result));

        return null;
    }


    /**
     * Recipes with mold Tag :
     *
     * @param moldTag  Mold used (with Tag)
     * @param fluid    Input
     * @param amount   Fluid amount
     * @param result   Output from Item
     * @param duration Processing time
     */

    protected GeneratedRecipe tableWithMoldTag(TagKey<Item> moldTag, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        ResourceLocation location = moldTag.location();
        create(CMRecipeTypes.CASTING_IN_TABLE, result, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(moldTag)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }

    protected GeneratedRecipe basinWithMoldTag(TagKey<Item> moldTag, boolean moldConsumed, FluidEntry<BaseFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        ResourceLocation location = moldTag.location();
        create(CMRecipeTypes.CASTING_IN_BASIN, result, b -> b.duration(duration)
                .require(moldTag)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }
}