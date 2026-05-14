package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.EntityIngredientComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobMeltingRecipeSchema {

    private static final RecipeKey<?> entityKey = EntityIngredientComponent.TYPE.instance()
            .inputKey("entity");

    public static RecipeSchema getRecipeSchema(RecipeSchema parentSchema) {
        List<RecipeKey<?>> mergedKeys = new ArrayList<>(parentSchema.keys);
        mergedKeys.add(1, entityKey); // Add second index after "results"

        return new RecipeSchema(Map.of(), mergedKeys)
                .uniqueIds(List.of(entityKey));
    }
}