package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.EntityIngredient;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityIngredientComponent extends SimpleRecipeComponent<DamagedEntityIngredient> {
    public static final TypeInfo TYPE_INFO = TypeInfo.of(DamagedEntityIngredient.class);
    public static final RecipeComponentType<DamagedEntityIngredient> TYPE = RecipeComponentType.unit(CreateMetallurgy.asResource("entity_ingredient"), EntityIngredientComponent::new);

    public EntityIngredientComponent(RecipeComponentType<?> type) {
        super(type, DamagedEntityIngredient.CODEC, TYPE_INFO);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, Object from) {
        return from instanceof DamagedEntityIngredient ||
                from instanceof EntityIngredient ||
                from instanceof EntityType<?> ||
                from instanceof Entity;
    }

    @Override
    public boolean isEmpty(DamagedEntityIngredient value) {
        return value == DamagedEntityIngredient.EMPTY || value.getEntities().isEmpty();
    }

    @Override
    public DamagedEntityIngredient wrap(RecipeScriptContext cx, Object from) {
        return EntityIngredientWrapper.wrap(cx.cx(), from);
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, DamagedEntityIngredient value) {
        if (value != null && value.getIngredient() != null) {
            // Use the underlying entity ingredient for unique ID
            var entities = value.getEntities();
            if (!entities.isEmpty()) {
                builder.append(entities.getFirst().getDescriptionId());
            }
            builder.append(String.valueOf(value.getDamage()));
        }
    }
}
