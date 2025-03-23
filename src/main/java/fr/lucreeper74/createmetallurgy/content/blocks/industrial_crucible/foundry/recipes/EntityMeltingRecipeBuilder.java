package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityMeltingRecipeBuilder extends FoundryRecipeBuilder<EntityMeltingRecipe> {

    protected EntityIngredient entityIngredient;

    public EntityMeltingRecipeBuilder(ProcessingRecipeFactory<EntityMeltingRecipe> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    // Datagen shortcuts

    public EntityMeltingRecipeBuilder requireEntity(EntityType<?> type, int damage) {
        return requireEntity(EntityIngredient.fromType(type, damage));
    }

    public EntityMeltingRecipeBuilder requireEntity(TagKey<EntityType<?>> tag, int damage) {
        return requireEntity(EntityIngredient.fromTag(tag, damage));
    }

    public EntityMeltingRecipeBuilder requireEntity(EntityIngredient entityIngredient) {
        this.entityIngredient = entityIngredient;
        return this;
    }

    // Build Datagen
    @Override
    public EntityMeltingRecipe build() {
        EntityMeltingRecipe recipe = super.build();
        recipe.entityIngredient = entityIngredient;
        return recipe;
    }
}
