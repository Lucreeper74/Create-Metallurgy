package fr.lucreeper74.createmetallurgy.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.CastingOutputComponent;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.CastingOutputWrapper;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.EntityIngredientComponent;
import fr.lucreeper74.createmetallurgy.compat.kubejs.components.EntityIngredientWrapper;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.CastingRecipeSchema;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.FoundryRecipeSchema;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.MobMeltingRecipeSchema;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import net.minecraft.resources.ResourceLocation;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.CREATEJS_LOADED;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.KUBEJS_LOADED;

@SuppressWarnings("unused")
public class CreateMetallurgyKJS implements KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {

        if (KUBEJS_LOADED && !CREATEJS_LOADED)
            return; // Prevent recipeSchema from loading if CreateJS not present

        // Regular processing recipes schemes
        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("alloying", ResourceLocation.fromNamespaceAndPath("create", "base/processing"));
        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("grinding", ResourceLocation.fromNamespaceAndPath("create", "base/processing_with_time"));
        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("melting", ResourceLocation.fromNamespaceAndPath("create", "base/processing_with_time"));

        // Casting recipes schemes
        registry.register(CreateMetallurgy.asResource("base/casting"), CastingRecipeSchema.getRecipeSchema());
        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("casting_in_table", CreateMetallurgy.asResource("base/casting"));
        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("casting_in_basin", CreateMetallurgy.asResource("base/casting"));

        // Foundry recipes schemes
        registry.register(CreateMetallurgy.asResource("base/foundry"), FoundryRecipeSchema.getRecipeSchema());
        registry.register(CreateMetallurgy.asResource("base/foundry_with_time"), FoundryRecipeSchema.getRecipeSchemaWithTime());

        registry.namespace(CreateMetallurgy.MOD_ID)
                .withExistingParent("bulk_melting", CreateMetallurgy.asResource("base/foundry_with_time"));
        registry.register(CreateMetallurgy.asResource("entity_melting"), MobMeltingRecipeSchema.getRecipeSchema(registry.namespace(CreateMetallurgy.MOD_ID)
                .getRegisteredOrThrow("base/foundry").schema));
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
        registry.register(CastingOutputComponent.TYPE);
        registry.register(EntityIngredientComponent.TYPE);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(CastingOutput.class, CastingOutputWrapper::wrapCastingOutput);
        registry.register(DamagedEntityIngredient.class, EntityIngredientWrapper::wrapEntityIngredient);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("CastingOutput", CastingOutputWrapper.class);
        bindings.add("EntityIngredient", EntityIngredientWrapper.class);
    }
}