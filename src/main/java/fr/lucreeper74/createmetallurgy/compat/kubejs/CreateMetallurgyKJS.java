package fr.lucreeper74.createmetallurgy.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipe.CastingOutputComponent;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipe.CastingOutputWrapper;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipe.EntityIngredientComponent;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;

@SuppressWarnings("unused")
public class CreateMetallurgyKJS implements KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        // Casting In Table
        // Casting In Basin
        // Grinding
        // Alloying
        // Melting
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
        registry.register(CastingOutputComponent.TYPE);
        registry.register(EntityIngredientComponent.TYPE);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(CastingOutput.class, CastingOutputWrapper::wrapCastingOutput);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("CastingOutput", CastingOutputWrapper.class);
    }
}