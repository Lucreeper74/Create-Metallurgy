package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.data.SimpleDatagenIngredient;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.Pair;
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
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CastingRecipeBuilder {

    private CastingRecipe recipe;

    public CastingRecipeBuilder(CMRecipeTypes type, ResourceLocation id) {
        switch (type) {
            case CASTING_IN_TABLE -> this.recipe = new CastingTableRecipe(id);
            case CASTING_IN_BASIN -> this.recipe = new CastingBasinRecipe(id);
            default -> throw new IllegalArgumentException("Recipe type '" + type + "' its not a Casting Recipe");
        }
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

    // For Output
    public CastingRecipeBuilder output(ItemLike item) {
        return output(item, 1);
    }

    public CastingRecipeBuilder output(float chance, ItemLike item) {
        return output(chance, item, 1);
    }

    public CastingRecipeBuilder output(ItemLike item, int amount) {
        return output(1, item, amount);
    }

    public CastingRecipeBuilder output(float chance, ItemLike item, int amount) {
        return output(chance, new ItemStack(item, amount));
    }

    public CastingRecipeBuilder output(ItemStack output) {
        return output(1, output);
    }

    public CastingRecipeBuilder output(float chance, ItemStack output) {
        return output(new ProcessingOutput(output, chance));
    }

    public CastingRecipeBuilder output(float chance, Mods mod, String id, int amount) {
        return output(new ProcessingOutput(Pair.of(mod.asResource(id), amount), chance));
    }

    public CastingRecipeBuilder output(float chance, ResourceLocation registryName, int amount) {
        return output(new ProcessingOutput(Pair.of(registryName, amount), chance));
    }

    public CastingRecipeBuilder output(ProcessingOutput output) {
        recipe.result = output;
        return this;
    }

    // Others
    public CastingRecipeBuilder duration(int ticks) {
        recipe.processingDuration = ticks;
        return this;
    }

    public CastingRecipeBuilder withMoldConsumed() {
        recipe.moldConsumed = true;
        return this;
    }

    // Build Datagen
    public CastingRecipe build() {
        return recipe;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new DataGenResult(build()));
    }

    public static class DataGenResult implements FinishedRecipe {

        private CastingRecipe recipe;
        private List<ICondition> recipeConditions;
        private ResourceLocation id;
        private CastingRecipeSerializer serializer;

        public DataGenResult(CastingRecipe recipe) {
            this.recipe = recipe;
            this.id = new ResourceLocation(recipe.getId().getNamespace(),
                    this.recipe.getTypeInfo().getId().getPath() + "/" + recipe.getId().getPath());
            this.serializer = (CastingRecipeSerializer) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            serializer.write(json, recipe);
//            if (recipeConditions.isEmpty())
//                return;
//
//            JsonArray conds = new JsonArray();
//            recipeConditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
//            json.add("conditions", conds);
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