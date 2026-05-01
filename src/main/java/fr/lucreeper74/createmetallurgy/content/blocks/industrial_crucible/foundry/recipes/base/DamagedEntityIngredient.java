package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import javax.annotation.Nullable;
import java.util.List;

public final class DamagedEntityIngredient {

    public static final Codec<DamagedEntityIngredient> CODEC = RecordCodecBuilder.create(i -> i.group(
                    EntityIngredient.CODEC.forGetter(s -> s.ingredient),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.NON_NEGATIVE_INT, "damage", 1).forGetter(s -> s.damage))
            .apply(i, DamagedEntityIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DamagedEntityIngredient> STREAM_CODEC = StreamCodec.composite(
            EntityIngredient.STREAM_CODEC, DamagedEntityIngredient::getIngredient,
            ByteBufCodecs.VAR_INT, DamagedEntityIngredient::getDamage,
            DamagedEntityIngredient::new);

    public static final DamagedEntityIngredient EMPTY = new DamagedEntityIngredient(null, 0);

    private final EntityIngredient ingredient;
    private final int damage;

    @Nullable
    private List<EntityType<?>> entities;

    public DamagedEntityIngredient(EntityIngredient ingredient, int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative");
        }
        this.ingredient = ingredient;
        this.damage = damage;
    }

    public static DamagedEntityIngredient fromTag(TagKey<EntityType<?>> tag, int damage) {
        return new DamagedEntityIngredient(new EntityIngredient.EntityTagIngredient(tag), damage);
    }

    public static DamagedEntityIngredient fromType(EntityType<?> type, int damage) {
        return new DamagedEntityIngredient(new EntityIngredient.EntityTypeIngredient(type), damage);
    }

    public static DamagedEntityIngredient fromEntity(Entity entity, int damage) {
        return fromType(entity.getType(), damage);
    }

    public int getDamage() {
        return damage;
    }

    public EntityIngredient getIngredient() {
        return ingredient;
    }

    public boolean test(EntityType<?> entityType) {
        return ingredient.test(entityType);
    }

    public List<EntityType<?>> getEntities() {
        if (entities != null)
            return entities;
        return entities = ingredient.getEntities();
    }

    /* For JEI purposes */
    private List<EntityStack> display;

    public List<EntityStack> getDisplay() {
        if (display == null)
            display = EntityStack.wrap(getEntities());
        return display;
    }

    public record EntityStack(EntityType<?> type) {
        public static final Codec<EntityStack> CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                .xmap(EntityStack::new, EntityStack::type);

        public static List<EntityStack> wrap(List<EntityType<?>> types) {
            return types.stream().map(EntityStack::new).toList();
        }
    }
}
