package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class EntityIngredient implements Predicate<EntityType<?>> {

    public static final MapCodec<EntityIngredient> CODEC = NeoForgeExtraCodecs.xor(EntityTypeIngredient.CODEC, EntityTagIngredient.CODEC).xmap(either -> either.map(id -> id, id -> id), ingredient -> {
        if (ingredient instanceof EntityTypeIngredient type)
            return Either.left(type);
        else if (ingredient instanceof EntityTagIngredient tag)
            return Either.right(tag);
        throw new IllegalStateException("Entity ingredient should be either a type or a tag!");
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityIngredient> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EntityIngredient decode(RegistryFriendlyByteBuf buf) {
            byte id = buf.readByte();
            return switch (id) {
                case 0 -> new EntityTypeIngredient(ByteBufCodecs.registry(Registries.ENTITY_TYPE)
                        .decode(buf));
                case 1 -> new EntityTagIngredient(TagKey.create(Registries.ENTITY_TYPE, buf.readResourceLocation()));
                default -> throw new IllegalStateException("Invalid ingredient id: " + id);
            };
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, EntityIngredient ingredient) {
            if (ingredient instanceof EntityTypeIngredient typeIngredient) {
                buf.writeByte(0);
                ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(buf, typeIngredient.entityType);
            } else if (ingredient instanceof EntityTagIngredient tagIngredient) {
                buf.writeByte(1);
                buf.writeResourceLocation(tagIngredient.tag.location());
            } else {
                throw new IllegalStateException("Unknown EntityIngredient type");
            }
        }
    };

    private List<EntityType<?>> entities;

    protected abstract boolean testInternal(EntityType<?> type);

    protected abstract Stream<EntityType<?>> determineEntities();

    public List<EntityType<?>> getEntities() {
        if (entities != null)
            return entities;
        return entities = determineEntities().toList();
    }

    @Override
    public boolean test(EntityType<?> type) {
        if (type == null)
            throw new IllegalArgumentException("Entity Type cannot be null!");
        return testInternal(type);
    }

    public static class EntityTypeIngredient extends EntityIngredient {
        public static final MapCodec<EntityTypeIngredient> CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                .xmap(EntityTypeIngredient::new, s -> s.entityType).fieldOf("type");

        protected EntityType<?> entityType;

        public EntityTypeIngredient(EntityType<?> type) {
            this.entityType = type;
        }

        @Override
        public boolean testInternal(EntityType<?> entityType) {
            return this.entityType.equals(entityType);
        }

        @Override
        protected Stream<EntityType<?>> determineEntities() {
            return Stream.of(entityType);
        }
    }

    public static class EntityTagIngredient extends EntityIngredient {
        public static final MapCodec<EntityTagIngredient> CODEC = TagKey.codec(Registries.ENTITY_TYPE)
                .xmap(EntityTagIngredient::new, s -> s.tag).fieldOf("tag");

        private final TagKey<EntityType<?>> tag;

        public EntityTagIngredient(TagKey<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean testInternal(EntityType<?> entityType) {
            if (tag != null && entityType != null)
                return entityType.is(tag);
            return false;
        }

        @Override
        protected Stream<EntityType<?>> determineEntities() {
            return BuiltInRegistries.ENTITY_TYPE.getTag(tag)
                    .stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value);
        }
    }
}