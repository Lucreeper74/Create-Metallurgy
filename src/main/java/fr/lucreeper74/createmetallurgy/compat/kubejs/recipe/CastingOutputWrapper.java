package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public interface CastingOutputWrapper {

    @Info("Returns a CastingOutput of the ItemStack or ItemTag")
    static CastingOutput of(String string) {
        return of(string, 1);
    }

    @Info("Returns a CastingOutput of the ItemStack or ItemTag with count")
    static CastingOutput of(String string, int count) {
        StringReader reader = new StringReader(string);
        reader.skipWhitespace();

        if (!reader.canRead() ||
                string.isBlank() ||
                string.equals("air") ||
                string.equals("minecraft:air")) {
            return CastingOutput.EMPTY;
        }

        DataResult<CastingOutput> dataResult;
        if (reader.peek() == '#') {
            reader.skip();
            dataResult = ID.read(reader).map(ItemTags::create).map(tag -> CastingOutput.fromTag(tag, count));
        } else
            dataResult = ID.read(reader).flatMap(ItemWrapper::findItem)
                    .map(item -> CastingOutput.fromStack(new ItemStack(item.value(), count)));

        return dataResult.getOrThrow(error -> new KubeRuntimeException(
                ("Failed to read CastingOutput from %s: %s").formatted(string, error)));
    }

    @HideFromJS
    static CastingOutput wrapCastingOutput(Context cx, @Nullable Object from) {
        return switch (from) {
            case null -> CastingOutput.EMPTY;
            case CastingOutput co -> co;
            case ItemStack s -> s.isEmpty() ? CastingOutput.EMPTY : CastingOutput.fromStack(s);
            case ItemLike i when i.asItem() == Items.AIR -> CastingOutput.EMPTY;
            case ItemLike i -> CastingOutput.fromStack(new ItemStack(i.asItem()));
            case TagKey<?>(ResourceKey<?> reg, ResourceLocation location) ->
                    CastingOutput.fromTag(ItemTags.create(location), 1);
            // TODO: maybe a custom string-like type tags wrapper ("#c:ingots")
            default -> CastingOutput.fromStack(ItemWrapper.wrap(cx, from));
        };
    }
}
