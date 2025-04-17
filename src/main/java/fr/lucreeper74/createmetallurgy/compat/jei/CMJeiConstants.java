package fr.lucreeper74.createmetallurgy.compat.jei;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientType;

public class CMJeiConstants {

    public static final IIngredientType<EntityIngredient.EntityInput> ENTITY_TYPE = () -> EntityIngredient.EntityInput.class;
}