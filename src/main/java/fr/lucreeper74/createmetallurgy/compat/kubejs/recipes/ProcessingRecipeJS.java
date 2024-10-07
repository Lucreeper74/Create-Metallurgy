package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ProcessingRecipeJS {

    RecipeKey<Either<OutputFluid, OutputItem>[]> OUTPUT = FluidComponents.OUTPUT_OR_ITEM_ARRAY.key("results");
    RecipeKey<Either<InputFluid, InputItem>[]> INPUT = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime").optional(100d);

    RecipeKey<String> HEAT = new StringComponent("Not a valid heat condition!", s -> {
        for (var h : HeatCondition.values()) {
            if (h.name().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }).key("heatRequirement").defaultOptional().allowEmpty();

    RecipeSchema WITH_HEAT = new RecipeSchema(OUTPUT, INPUT, TIME, HEAT);
    RecipeSchema BASIC = new RecipeSchema(OUTPUT, INPUT, TIME);
}
