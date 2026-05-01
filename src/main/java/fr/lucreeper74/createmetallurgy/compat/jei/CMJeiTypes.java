package fr.lucreeper74.createmetallurgy.compat.jei;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import mezz.jei.api.ingredients.IIngredientType;

public class CMJeiTypes {

    public static final IIngredientType<DamagedEntityIngredient.EntityStack> ENTITY_STACK = new IIngredientType<>() {

        @Override
        public String getUid() {
            return "entity_stack";
        }

        @Override
        public Class<DamagedEntityIngredient.EntityStack> getIngredientClass() {
            return DamagedEntityIngredient.EntityStack.class;
        }
    };

    private CMJeiTypes() {
    }
}