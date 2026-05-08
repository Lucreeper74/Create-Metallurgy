package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.EntityIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

public interface EntityIngredientWrapper {

    @HideFromJS
    static DamagedEntityIngredient wrap(Context cx, @Nullable Object from) {
        return switch (from) {
            case null -> DamagedEntityIngredient.EMPTY;
            case DamagedEntityIngredient dei -> dei;
            case EntityIngredient ei -> new DamagedEntityIngredient(ei, 1);
            case EntityType<?> type -> DamagedEntityIngredient.fromType(type, 1);
            case Entity entity -> DamagedEntityIngredient.fromEntity(entity, 1);
            case TagKey<?>(ResourceKey<?> reg, ResourceLocation location) ->
                    DamagedEntityIngredient.fromTag(TagKey.create(Registries.ENTITY_TYPE, location), 1);

            default -> throw new IllegalArgumentException("Cannot convert " + from + " to DamagedEntityIngredient");
        };
    }
}
