package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.EntityIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

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
    public boolean matches(RecipeMatchContext cx, DamagedEntityIngredient ingredient, ReplacementMatchInfo match) {
        return match.match() instanceof EntityMatch m && m.matches(cx, ingredient, match.exact());
    }

    @Override
    public boolean isEmpty(DamagedEntityIngredient value) {
        return value == DamagedEntityIngredient.EMPTY || value.getEntities().isEmpty();
    }

    @Override
    public DamagedEntityIngredient replace(RecipeScriptContext cx, DamagedEntityIngredient original, ReplacementMatchInfo match, Object with) {
        if (matches(cx, original, match)) {
            return switch (with) {
                case DamagedEntityIngredient dei -> dei;
                case EntityType<?> type -> DamagedEntityIngredient.fromType(type, 1);
                default -> {
                    DamagedEntityIngredient dei = EntityIngredientWrapper.wrapEntityIngredient(cx.cx(), with);

                    if (!dei.getEntities().isEmpty() &&
                            !dei.getEntities().equals(original.getEntities()))
                        yield dei;
                    yield original;
                }
            };
        }
        return original;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, DamagedEntityIngredient dei) {
        if (dei != null && dei.getIngredient() != null) {
            // Use the underlying entity ingredient for unique ID
            builder.append("from");
            if (dei.getIngredient() instanceof EntityIngredient.EntityTagIngredient tagEi) {
                builder.append(tagEi.getTag().location());

            } else {
                List<EntityType<?>> entities = dei.getEntities();
                if (!entities.isEmpty())
                    builder.append(BuiltInRegistries.ENTITY_TYPE.getKey(entities.getFirst()).getNamespace());
            }
        }
    }
}