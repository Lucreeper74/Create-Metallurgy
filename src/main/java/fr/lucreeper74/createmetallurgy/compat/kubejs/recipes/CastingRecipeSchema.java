package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CastingRecipeSchema {
    RecipeKey<OutputItem> RESULTS = ItemComponents.OUTPUT.key("result");
    RecipeKey<Either<InputFluid, InputItem>[]> INGREDIENTS = FluidComponents.INPUT_OR_ITEM_ARRAY.key("ingredients");
    RecipeKey<Long> PROCESSING_TIME = TimeComponent.TICKS.key("processingTime").optional(100L);
    RecipeKey<Boolean> MOLD_CONSUMED = BooleanComponent.BOOLEAN.key("mold_consumed").optional(false);

    RecipeSchema DEFAULT = new RecipeSchema(RESULTS, INGREDIENTS, PROCESSING_TIME, MOLD_CONSUMED);
}