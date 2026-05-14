package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.create.recipe.CreateRecipeComponents;
import dev.latvian.mods.kubejs.create.recipe.ProcessingOutputRecipeComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams.DEFAULT_MAX_HEAT;
import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams.DEFAULT_MIN_HEAT;

@SuppressWarnings("unused")
public class FoundryRecipeSchema {

    private static final RecipeKey<List<Either<FluidStack, ProcessingOutput>>> resultsKey = FluidStackComponent.FLUID_STACK.instance()
            .or(ProcessingOutputRecipeComponent.TYPE.instance())
            .asList()
            .outputKey("results");

    private static final RecipeKey<List<Either<SizedFluidIngredient, Ingredient>>> ingredientsKey = CreateRecipeComponents.SIZED_FLUID_INGREDIENT.instance()
            .or(IngredientComponent.INGREDIENT.instance())
            .asList()
            .withSpread(Optional.of(IgnoreComponent.TYPE.instance().or(SizedIngredientComponent.SIZED_INGREDIENT.instance())))
            .inputKey("ingredients");

    private static final RecipeKey<TickDuration> processingTimeKey = TimeComponent.TICKS
            .inputKey("processing_time")
            .optional(TickDuration.of(100));

    private static final RecipeKey<Integer> minHeatRequirementKey = NumberComponent.INT
            .inputKey("minHeatRequirement")
            .optional(DEFAULT_MIN_HEAT)
            .exclude();

    private static final RecipeKey<Integer> maxHeatRequirementKey = NumberComponent.INT
            .inputKey("maxHeatRequirement")
            .optional(DEFAULT_MAX_HEAT)
            .exclude();

    public static RecipeSchema getRecipeSchema() {
        return new RecipeSchema(resultsKey,
                ingredientsKey,
                processingTimeKey,
                minHeatRequirementKey,
                maxHeatRequirementKey
        )
                .uniqueIds(List.of(resultsKey));
    }


    // With time always write
    private static final RecipeKey<TickDuration> processingTimeAlwaysKey = TimeComponent.TICKS
            .inputKey("processing_time")
            .optional(TickDuration.of(100))
            .alwaysWrite();

    public static RecipeSchema getRecipeSchemaWithTime() {
        RecipeSchema parentSchema = getRecipeSchema();
        List<RecipeKey<?>> mergedKeys = new ArrayList<>(parentSchema.keys);
        mergedKeys.set(2, processingTimeAlwaysKey);

        return new RecipeSchema(Map.of(), mergedKeys)
                .uniqueIds(List.of(parentSchema.getKey("results")));
    }
}
