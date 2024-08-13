package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;

import static com.simibubi.create.AllTags.optionalTag;

public abstract class CastingOutput {
    public static final CastingOutput EMPTY = CastingOutput.fromStack(ItemStack.EMPTY);

    public abstract ItemStack getStack();

    public abstract JsonElement serialize();

    public static CastingOutput fromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        return new StackOutput(stack);
    }

    public static CastingOutput fromTag(TagKey<Item> tag, int count) {
        return new TagOutput(tag, count);
    }

    public static CastingOutput deserialize(JsonElement je) {
        if (!je.isJsonObject())
            throw new JsonSyntaxException("CastingOutput must be a json object");

        JsonObject json = je.getAsJsonObject();
        int count = GsonHelper.getAsInt(json, "count", 1);
        if (json.has("item")) {
            String itemId = GsonHelper.getAsString(json, "item");
            ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), count);
            return CastingOutput.fromStack(itemstack);
        } else if (json.has("tag")) {
            String rawTag = GsonHelper.getAsString(json, "tag");
            TagKey<Item> tag = optionalTag(ForgeRegistries.ITEMS, new ResourceLocation(rawTag));
            return CastingOutput.fromTag(tag, count);
        } else throw new JsonParseException("An CastingOutput entry needs either a tag or an item");
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeItem(getStack());
    }

    public static CastingOutput read(FriendlyByteBuf buf) {
        return CastingOutput.fromStack(buf.readItem());
    }


    /**
     * Class for CastingOutput from an ItemStack
     */
    private static class StackOutput extends CastingOutput {
        private final ItemStack stack;

        private StackOutput(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public JsonElement serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
            int count = stack.getCount();
            if (count > 1)
                json.addProperty("count", count);
            return json;
        }
    }


    /**
     * Class for CastingOutput from a Tag
     */
    private static class TagOutput extends CastingOutput {
        private final TagKey<Item> tag;
        private final int count;

        private TagOutput(TagKey<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        @Override
        public ItemStack getStack() {
            Iterator<Holder<Item>> items = Registry.ITEM.getTagOrEmpty(tag).iterator();
            if (items.hasNext())
                return new ItemStack(items.next(), count);
            else
                return new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER)
                        .setHoverName(net.minecraft.network.chat.Component.literal("Empty Tag: " + this.tag.location()));
        }

        @Override
        public JsonElement serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("tag", tag.location().toString());
            if (count > 1)
                json.addProperty("count", count);
            return json;
        }
    }
}