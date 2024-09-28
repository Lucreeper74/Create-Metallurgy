package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CastingRecipeJS {
    RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("result");
    RecipeKey<Either<InputFluid, InputItem>[]> INPUT = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime").optional(100d);
    RecipeKey<Boolean> CAST = BooleanComponent.BOOLEAN.key("mold_consumed").optional(false);

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, TIME, CAST);
}