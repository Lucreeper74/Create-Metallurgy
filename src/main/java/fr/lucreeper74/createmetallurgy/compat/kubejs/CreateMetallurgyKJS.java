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
            CMRecipeTypes.CASTING_IN_BASIN, CastingRecipeSchema.DEFAULT,
            CMRecipeTypes.CASTING_IN_TABLE, CastingRecipeSchema.DEFAULT,
            CMRecipeTypes.GRINDING, ProcessingRecipeSchema.PROCESSING_WITH_TIME,
            CMRecipeTypes.ALLOYING, ProcessingRecipeSchema.PROCESSING_DEFAULT,
            CMRecipeTypes.MELTING, ProcessingRecipeSchema.PROCESSING_DEFAULT
    );

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        for (var recipeType : recipeSchemas.keySet()) {
            var schema = recipeSchemas.get(recipeType);
            event.register(recipeType.getId(), schema);
        }
    }
}