package fr.lucreeper74.createmetallurgy.compat.jei;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientType;

public class CMJeiTypes {

    public static final IIngredientType<EntityIngredient.EntityStack> ENTITY_STACK = new IIngredientType<>() {

        @Override
        public String getUid() {
            return "entity_stack";
        }

        @Override
        public Class<EntityIngredient.EntityStack> getIngredientClass() {
            return EntityIngredient.EntityStack.class;
        }
    };

    private CMJeiTypes() {
    }
}