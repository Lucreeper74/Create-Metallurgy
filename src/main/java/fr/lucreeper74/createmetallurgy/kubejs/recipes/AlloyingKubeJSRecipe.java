package fr.lucreeper74.createmetallurgy.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface AlloyingKubeJSRecipe {
    RecipeKey<Either<OutputFluid, OutputItem>[]> OUTPUT = FluidComponents.OUTPUT_OR_ITEM_ARRAY.key("results");
    RecipeKey<Either<InputFluid, InputItem>[]> INPUT = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime").optional(100d);
    RecipeKey<String> HEAT = StringComponent.NON_EMPTY.key("heatRequirement").optional("heated").optional("superheated");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, TIME, HEAT);
}