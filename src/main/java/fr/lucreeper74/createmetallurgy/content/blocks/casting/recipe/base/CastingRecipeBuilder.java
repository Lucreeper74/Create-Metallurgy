package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base;

import com.google.common.base.Joiner;
import com.simibubi.create.foundation.data.SimpleDatagenIngredient;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingTableRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CastingRecipeBuilder {

    protected ResourceLocation recipeId;
    private CastingRecipe recipe;
    protected List<ICondition> recipeConditions;

    public CastingRecipeBuilder(CMRecipeTypes type, ResourceLocation recipeId) {
        switch (type) {
            case CASTING_IN_TABLE -> this.recipe = new CastingTableRecipe();
            case CASTING_IN_BASIN -> this.recipe = new CastingBasinRecipe();
            default -> throw new IllegalArgumentException("Recipe type '" + type + "' its not a Casting Recipe");
        }
        this.recipeId = recipeId;
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
        recipe.ingredient = new SimpleDatagenIngredient(mod, id).toVanilla();
        return this;
    }

    public CastingRecipeBuilder require(FlowingFluid fluid, int amount) {
        return require(SizedFluidIngredient.of(fluid.getSource(), amount));
    }

    public CastingRecipeBuilder require(TagKey<Fluid> fluidTag, int amount) {
        return require(SizedFluidIngredient.of(fluidTag, amount));
    }

    public CastingRecipeBuilder require(SizedFluidIngredient ingredient) {
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

    public void build(RecipeOutput consumer) {
        CastingRecipe recipe = build();
        IRecipeTypeInfo recipeType = recipe.getTypeInfo();
        ResourceLocation typeId = recipeType.getId();
        ResourceLocation id = recipeId.withPrefix(typeId.getPath() + "/");
        var errors = recipe.validate();
        if (!errors.isEmpty()) {
            errors.add(recipe.getClass().getSimpleName() + "with id " + id + " failed validation:");
            CreateMetallurgy.LOGGER.warn(Joiner.on('\n').join(errors));
        }
        consumer.accept(id, recipe, null, recipeConditions.toArray(new ICondition[0]));
    }
}