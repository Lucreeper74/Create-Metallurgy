package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class EntityIngredient implements Predicate<EntityType<?>> {

    public static final EntityIngredient EMPTY = new EntityTypeIngredient();

    public List<EntityType<?>> entities;


    public static EntityIngredient fromTag(TagKey<EntityType<?>> tag, int damage) {
        EntityTagIngredient ingredient = new EntityTagIngredient();
        ingredient.tag = tag;
        ingredient.damage = damage;
        return ingredient;
    }

    public static EntityIngredient fromType(EntityType<?> type, int damage) {
        EntityTypeIngredient ingredient = new EntityTypeIngredient();
        ingredient.entityType = type;
        ingredient.damage = damage;
        return ingredient;
    }

    public static EntityIngredient fromEntity(Entity entity, int damage) {
        EntityTypeIngredient ingredient = new EntityTypeIngredient();
        ingredient.entityType = entity.getType();
        ingredient.damage = damage;
        return ingredient;
    }

    protected int damage = 1; // Default damage is 1
    protected abstract boolean testInternal(EntityType<?> type);

    protected abstract void readInternal(FriendlyByteBuf buffer);

    protected abstract void writeInternal(FriendlyByteBuf buffer);

    protected abstract void readInternal(JsonObject json);

    protected abstract void writeInternal(JsonObject json);

    protected abstract List<EntityType<?>> determineEntities();

    public int getDamage() {
        return damage;
    }

    public List<EntityType<?>> getEntities() {
        if (entities != null)
            return entities;
        return entities = determineEntities();
    }

    @Override
    public boolean test(EntityType<?> type) {
        if (type == null)
            throw new IllegalArgumentException("Entity Type cannot be null!");
        return testInternal(type);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this instanceof EntityTagIngredient);
        buffer.writeVarInt(damage);
        writeInternal(buffer);
    }

    public static EntityIngredient read(FriendlyByteBuf buffer) {
        boolean isTagIngredient = buffer.readBoolean();
        EntityIngredient ingredient = isTagIngredient ? new EntityTagIngredient() : new EntityTypeIngredient();
        ingredient.damage = buffer.readVarInt();
        ingredient.readInternal(buffer);
        return ingredient;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        writeInternal(json);
        json.addProperty("damage", damage);
        return json;
    }

    public static boolean isEntityIngredient(@Nullable JsonElement je) {
        if (je == null || je.isJsonNull())
            return false;
        if (!je.isJsonObject())
            return false;
        JsonObject json = je.getAsJsonObject();
        if (json.has("tag"))
            return true;
        else return json.has("type");
    }

    public static EntityIngredient deserialize(@Nullable JsonElement je) {
        if (!isEntityIngredient(je))
            throw new JsonSyntaxException("Invalid entity ingredient: " + je);

        JsonObject json = je.getAsJsonObject();
        EntityIngredient ingredient = json.has("tag") ? new EntityTagIngredient() : new EntityTypeIngredient();
        ingredient.readInternal(json);

        if (!json.has("damage"))
            throw new JsonSyntaxException("Entity ingredient has to define a damage");
        ingredient.damage = GsonHelper.getAsInt(json, "damage");
        return ingredient;
    }

    public static class EntityTypeIngredient extends EntityIngredient {

        protected EntityType<?> entityType;

        @Override
        public boolean testInternal(EntityType<?> entityType) {
            return this.entityType.equals(entityType);
        }

        @Override
        protected void readInternal(FriendlyByteBuf buffer) {
            entityType = buffer.readRegistryId();
        }

        @Override
        protected void writeInternal(FriendlyByteBuf buffer) {
            buffer.writeRegistryId(ForgeRegistries.ENTITY_TYPES, entityType);
        }

        @Override
        protected void readInternal(JsonObject json) {
            entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(json.get("type").getAsString()));
        }

        @Override
        protected void writeInternal(JsonObject json) {
            json.addProperty("type", EntityType.getKey(entityType)
                    .toString());
        }

        @Override
        protected List<EntityType<?>> determineEntities() {
            return List.of(entityType);
        }
    }

    public static class EntityTagIngredient extends EntityIngredient {

        protected TagKey<EntityType<?>> tag;

        @Override
        public boolean testInternal(EntityType<?> entityType) {
            if (tag != null && entityType != null)
                return entityType.is(tag);
            return false;
        }

        @Override
        protected void readInternal(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            entities = new ArrayList<>(size);
            for (int i = 0; i < size; i++)
                entities.add(ForgeRegistries.ENTITY_TYPES.getValue(buffer.readResourceLocation()));
        }

        @Override
        protected void writeInternal(FriendlyByteBuf buffer) {
            List<EntityType<?>> matchingEntities = getEntities();
            buffer.writeVarInt(matchingEntities.size());
            matchingEntities.forEach(type -> buffer.writeResourceLocation(EntityType.getKey(type)));
        }

        @Override
        protected void readInternal(JsonObject json) {
            ResourceLocation name = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            tag = TagKey.create(Registries.ENTITY_TYPE, name);
        }

        @Override
        protected void writeInternal(JsonObject json) {
            json.addProperty("tag", tag.location()
                    .toString());
        }

        @Override
        protected List<EntityType<?>> determineEntities() {
            return ForgeRegistries.ENTITY_TYPES.tags()
                    .getTag(tag).stream().collect(Collectors.toList());
        }
    }

    /* For JEI purposes */
    private List<EntityInput> display;
    public List<EntityInput> getDisplay() {
        if (display == null)
            display = EntityInput.wrap(getEntities());
        return display;
    }

    public record EntityInput(EntityType<?> type) {
        public static List<EntityInput> wrap(List<EntityType<?>> types) {
            return types.stream().map(EntityInput::new).toList();
        }
    }
}