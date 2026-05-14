package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.create.recipe.CreateRecipeComponents;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.CastingOutputComponent;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class CastingRecipeSchema {

    private static final RecipeKey<CastingOutput> resultsKey = CastingOutputComponent.TYPE
            .outputKey("result");

    private static final RecipeKey<List<Either<SizedFluidIngredient, Ingredient>>> ingredientsKey = CreateRecipeComponents.SIZED_FLUID_INGREDIENT.instance()
            .or(IngredientComponent.INGREDIENT.instance())
            .asList()
            .withSpread(Optional.of(IgnoreComponent.TYPE.instance().or(SizedIngredientComponent.SIZED_INGREDIENT.instance())))
            .inputKey("ingredients");

    private static final RecipeKey<TickDuration> processingTimeKey = TimeComponent.TICKS
            .inputKey("processing_time")
            .optional(TickDuration.of(100))
            .alwaysWrite();

    private static final RecipeKey<Boolean> moldConsumedKey = BooleanComponent.BOOLEAN
            .inputKey("mold_consumed")
            .optional(false);

    public static RecipeSchema getRecipeSchema() {
        return new RecipeSchema(resultsKey,
                ingredientsKey,
                processingTimeKey,
                moldConsumedKey
        )
                .uniqueIds(List.of(resultsKey))
                .setOpFunction("moldConsumed", moldConsumedKey, true);
    }
}
