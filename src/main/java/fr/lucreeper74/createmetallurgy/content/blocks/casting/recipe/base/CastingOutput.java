package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Iterator;

public abstract class CastingOutput {

    public static final CastingOutput EMPTY = CastingOutput.fromStack(ItemStack.EMPTY);

    public static final MapCodec<CastingOutput> CODEC = NeoForgeExtraCodecs.xor(StackOutput.CODEC, TagOutput.CODEC).xmap(either -> either.map(id -> id, id -> id), output -> {
        if (output instanceof StackOutput item)
            return Either.left(item);
        else if (output instanceof TagOutput tag)
            return Either.right(tag);
        throw new IllegalStateException("Casting output should be either an item or a tag!");
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingOutput> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CastingOutput decode(RegistryFriendlyByteBuf buf) {
            byte id = buf.readByte();
            return switch (id) {
                case 0 -> new StackOutput(ItemStack.STREAM_CODEC.decode(buf));
                case 1 -> new TagOutput(TagKey.create(Registries.ITEM, buf.readResourceLocation()), buf.readVarInt());
                default -> throw new IllegalStateException("Invalid ingredient id: " + id);
            };
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CastingOutput ingredient) {
            if (ingredient instanceof StackOutput stackOutput) {
                buf.writeByte(0);
                ItemStack.STREAM_CODEC.encode(buf, stackOutput.stack);
            } else if (ingredient instanceof TagOutput tagOutput) {
                buf.writeByte(1);
                buf.writeResourceLocation(tagOutput.tag.location());
                buf.writeVarInt(tagOutput.count);
            } else {
                throw new IllegalStateException("Unknown Output type");
            }
        }
    };

    public abstract ItemStack getStack();

    public static CastingOutput fromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        return new StackOutput(stack);
    }

    public static CastingOutput fromTag(TagKey<Item> tag, int count) {
        return new TagOutput(tag, count);
    }

    /**
     * Class for CastingOutput from an ItemStack
     */
    private static class StackOutput extends CastingOutput {
        public static final MapCodec<StackOutput> CODEC = ItemStack.CODEC
                .xmap(StackOutput::new, s -> s.stack).fieldOf("item");

        private final ItemStack stack;

        private StackOutput(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }
    }


    /**
     * Class for CastingOutput from a Tag
     */
    private static class TagOutput extends CastingOutput {
        public static final MapCodec<TagOutput> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(s -> s.tag),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(s -> s.count)
                ).apply(instance, TagOutput::new)
        );

        private final TagKey<Item> tag;
        private final int count;

        private TagOutput(TagKey<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        @Override
        public ItemStack getStack() {
            Iterator<Item> items = BuiltInRegistries.ITEM.getTag(tag)
                    .stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value).iterator();
            if (items.hasNext())
                return new ItemStack(items.next(), count);
            else {
                ItemStack stack = new ItemStack(Blocks.BARRIER);
                stack.set(DataComponents.CUSTOM_NAME, Component.literal("Empty Tag: " + this.tag.location()));
                return stack;
            }
        }
    }
}