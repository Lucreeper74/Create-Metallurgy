package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.data.SimpleDatagenIngredient;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.tterrag.registrate.util.DataIngredient;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CastingRecipeBuilder {

    private CastingRecipe recipe;
    protected List<ICondition> recipeConditions;

    public CastingRecipeBuilder(CMRecipeTypes type, ResourceLocation id) {
        switch (type) {
            case CASTING_IN_TABLE -> this.recipe = new CastingTableRecipe(id);
            case CASTING_IN_BASIN -> this.recipe = new CastingBasinRecipe(id);
            default -> throw new IllegalArgumentException("Recipe type '" + type + "' its not a Casting Recipe");
        }
        recipeConditions = new ArrayList<>();
    }

    // For Inputs
    public CastingRecipeBuilder require(TagKey<Item> tag) {
        return require(Ingredient.of(tag));
    }

    public CastingRecipeBuilder require(ItemLike item) {
        return require(Ingredient.of(item));
    }

    public CastingRecipeBuilder require(Ingredient ingredient) {
        recipe.ingredient = ingredient;
        return this;
    }

    public CastingRecipeBuilder require(Mods mod, String id) {
        recipe.ingredient = new SimpleDatagenIngredient(mod, id);
        return this;
    }

    public CastingRecipeBuilder require(ResourceLocation ingredient) {
        recipe.ingredient = DataIngredient.ingredient(null, ingredient);
        return this;
    }

    public CastingRecipeBuilder require(Fluid fluid, int amount) {
        return require(FluidIngredient.fromFluid(fluid, amount));
    }

    public CastingRecipeBuilder require(TagKey<Fluid> fluidTag, int amount) {
        return require(FluidIngredient.fromTag(fluidTag, amount));
    }

    public CastingRecipeBuilder require(FluidIngredient ingredient) {
        recipe.fluidIngredient = ingredient;
        return this;
    }

    // For Output ItemStack
    public CastingRecipeBuilder output(ItemLike item) {
        return output(item, 1);
    }

    public CastingRecipeBuilder output(ItemLike item, int amount) {
        return output(new ItemStack(item, amount));
    }

    public CastingRecipeBuilder output(ItemStack output) {
        return output(CastingOutput.fromStack(output));
    }

    // For Output Tags
    public CastingRecipeBuilder output(TagKey<Item> tag) {
        return output(tag, 1);
    }

    public CastingRecipeBuilder output(TagKey<Item> tag, int amount) {
        return output(CastingOutput.fromTag(tag, amount));
    }

    public CastingRecipeBuilder output(CastingOutput output) {
        recipe.result = output;
        return this;
    }

    // Others
    public CastingRecipeBuilder duration(int ticks) {
        recipe.processingDuration = ticks;
        return this;
    }

    public CastingRecipeBuilder withMoldConsumed(boolean condition) {
        recipe.moldConsumed = condition;
        return this;
    }

    public CastingRecipeBuilder whenModLoaded(String modid) {
        return withCondition(new ModLoadedCondition(modid));
    }

    public CastingRecipeBuilder withCondition(ICondition condition) {
        recipeConditions.add(condition);
        return this;
    }

    // Build Datagen
    public CastingRecipe build() {
        return recipe;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new DataGenResult(build(), recipeConditions));
    }

    public static class DataGenResult implements FinishedRecipe {

        private CastingRecipe recipe;
        private List<ICondition> recipeConditions;
        private ResourceLocation id;
        private CastingRecipeSerializer serializer;

        public DataGenResult(CastingRecipe recipe, List<ICondition> recipeConditions) {
            this.recipe = recipe;
            this.recipeConditions = recipeConditions;
            this.id = new ResourceLocation(recipe.getId().getNamespace(),
                    this.recipe.getTypeInfo().getId().getPath() + "/" + recipe.getId().getPath());
            this.serializer = (CastingRecipeSerializer) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            serializer.write(json, recipe);
            if (recipeConditions.isEmpty())
                return;

            JsonArray conds = new JsonArray();
            recipeConditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
            json.add("conditions", conds);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }

    }
}