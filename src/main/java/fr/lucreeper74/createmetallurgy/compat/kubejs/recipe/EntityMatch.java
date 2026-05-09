package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatch;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;

public interface EntityMatch extends ReplacementMatch {

    boolean matches(RecipeMatchContext cx, DamagedEntityIngredient ingredient, boolean exact);

    default boolean matches(RecipeMatchContext cx, Object value, boolean exact) {
        return value instanceof DamagedEntityIngredient ingredient && matches(cx, ingredient, exact);
    }
}