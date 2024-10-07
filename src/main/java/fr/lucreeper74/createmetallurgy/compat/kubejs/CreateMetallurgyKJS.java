package fr.lucreeper74.createmetallurgy.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.*;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

import java.util.Map;

@SuppressWarnings("unused")
public class CreateMetallurgyKJS extends KubeJSPlugin {

    private static final Map<CMRecipeTypes, RecipeSchema> recipeSchemas = Map.of(
            CMRecipeTypes.CASTING_IN_BASIN, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.CASTING_IN_TABLE, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.GRINDING, ProcessingRecipeJS.BASIC,
            CMRecipeTypes.ALLOYING, ProcessingRecipeJS.WITH_HEAT,
            CMRecipeTypes.MELTING, ProcessingRecipeJS.WITH_HEAT
    );

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        for (var recipeType : recipeSchemas.keySet()) {
            var schema = recipeSchemas.get(recipeType);
            event.register(recipeType.getId(), schema);
        }
    }
}