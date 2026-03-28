package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ProcessingRecipeSchema {

    RecipeKey<Either<OutputFluid, OutputItem>[]> RESULTS = FluidComponents.OUTPUT_OR_ITEM_ARRAY.key("results");
    RecipeKey<Either<InputFluid, InputItem>[]> INGREDIENTS = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");

    RecipeKey<Long> PROCESSING_TIME = TimeComponent.TICKS.key("processingTime").optional(100L);
    RecipeKey<Long> PROCESSING_TIME_REQUIRED = TimeComponent.TICKS.key("processingTime").optional(100L).alwaysWrite();

    RecipeKey<String> HEAT_REQUIREMENT = new StringComponent("Not a valid heat condition!", s -> {
        for (var h : HeatCondition.values()) {
            if (h.name().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }).key("heatRequirement").defaultOptional().allowEmpty();

    class ProcessingRecipeJS extends RecipeJS {

        public RecipeJS heated() {
            return setValue(HEAT_REQUIREMENT, HeatCondition.HEATED.serialize());
        }

        public RecipeJS superheated() {
            return setValue(HEAT_REQUIREMENT, HeatCondition.SUPERHEATED.serialize());
        }
    }

    RecipeSchema PROCESSING_DEFAULT = new RecipeSchema(ProcessingRecipeJS.class, ProcessingRecipeJS::new, RESULTS, INGREDIENTS, PROCESSING_TIME, HEAT_REQUIREMENT);
    RecipeSchema PROCESSING_WITH_TIME = new RecipeSchema(ProcessingRecipeJS.class, ProcessingRecipeJS::new, RESULTS, INGREDIENTS, PROCESSING_TIME_REQUIRED, HEAT_REQUIREMENT);
}
