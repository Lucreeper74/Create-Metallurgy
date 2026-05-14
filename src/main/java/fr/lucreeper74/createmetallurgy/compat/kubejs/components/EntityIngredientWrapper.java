package fr.lucreeper74.createmetallurgy.compat.kubejs.components;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface EntityIngredientWrapper {

    @Info("Returns a EntityIngredient of the EntityType or EntityTag")
    static DamagedEntityIngredient of(String string) {
        return of(string, 1);
    }

    @Info("Returns a EntityIngredient of the EntityType or EntityTag with damage")
    static DamagedEntityIngredient of(String string, int damage) {
        StringReader reader = new StringReader(string);
        reader.skipWhitespace();

        if (!reader.canRead() ||
                string.isBlank() ||
                string.equals("none")) {
            return DamagedEntityIngredient.EMPTY;
        }

        DataResult<DamagedEntityIngredient> dataResult;
        if (reader.peek() == '#') {
            reader.skip();
            dataResult = ID.read(reader).map(resource -> TagKey.create(Registries.ENTITY_TYPE, resource))
                    .map(tag -> DamagedEntityIngredient.fromTag(tag, damage));
        } else
            dataResult = ID.read(reader).flatMap(EntityIngredientWrapper::findEntityType)
                    .map(entityType -> DamagedEntityIngredient.fromType(entityType.value(), damage));

        return dataResult.getOrThrow(error -> new KubeRuntimeException(
                ("Failed to read CastingOutput from %s: %s").formatted(string, error)));
    }

    @HideFromJS
    static DamagedEntityIngredient wrapEntityIngredient(Context cx, @Nullable Object from) {
        return switch (from) {
            case null -> DamagedEntityIngredient.EMPTY;
            case DamagedEntityIngredient co -> co;
            case EntityType<?> type -> DamagedEntityIngredient.fromType(type, 1);
            case Entity entity -> DamagedEntityIngredient.fromEntity(entity, 1);
            case TagKey<?>(ResourceKey<?> reg, ResourceLocation location) ->
                    DamagedEntityIngredient.fromTag(TagKey.create(Registries.ENTITY_TYPE, location), 1);
            default -> throw new KubeRuntimeException("Could not parse entity ingredient!").source(SourceLine.of(cx));
        };
    }

    @HideFromJS
    static DataResult<Holder<EntityType<?>>> findEntityType(ResourceLocation id) {
        return BuiltInRegistries.ENTITY_TYPE
                .getHolder(id)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "EntityType with ID " + id + " does not exist!"))
                .map(Function.identity());
    }
}
