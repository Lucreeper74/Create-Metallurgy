package fr.lucreeper74.createmetallurgy.kubejs.recipes;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface GrindingKubeJSRecipe {
    RecipeKey<OutputItem[]> OUTPUT = ItemComponents.OUTPUT_ARRAY.key("results");
    RecipeKey<InputItem[]> INPUT = ItemComponents.INPUT_ARRAY.key("ingredients");
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime").optional(100d);

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, TIME);
}