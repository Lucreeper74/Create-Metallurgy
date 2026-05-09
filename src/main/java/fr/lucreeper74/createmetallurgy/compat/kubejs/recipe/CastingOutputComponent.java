package fr.lucreeper74.createmetallurgy.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class CastingOutputComponent extends SimpleRecipeComponent<CastingOutput> {
    public static final TypeInfo TYPE_INFO = TypeInfo.of(CastingOutput.class);
    public static final RecipeComponentType<CastingOutput> TYPE = RecipeComponentType.unit(CreateMetallurgy.asResource("casting_output"), CastingOutputComponent::new);

    public CastingOutputComponent(RecipeComponentType<?> type) {
        super(type, CastingOutput.CODEC.codec(), TYPE_INFO);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, Object from) {
        return from instanceof CastingOutput || ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, CastingOutput value, ReplacementMatchInfo match) {
        return match.match() instanceof ItemMatch m && m.matches(cx, value.getStack(), match.exact());
    }

    @Override
    public boolean isEmpty(CastingOutput value) {
        return value == CastingOutput.EMPTY || value.getStack().isEmpty();
    }

    @Override
    public CastingOutput replace(RecipeScriptContext cx, CastingOutput original, ReplacementMatchInfo match, Object with) {
        if (matches(cx, original, match)) {
            return switch (with) {
                case CastingOutput output -> output;
                case ItemStack stack -> CastingOutput.fromStack(stack);
                default -> {
                    var output = CastingOutputWrapper.wrapCastingOutput(cx.cx(), with);

                    if (!output.getStack().isEmpty() &&
                            !ItemStack.isSameItemSameComponents(output.getStack(), original.getStack()))
                        yield output;
                    yield original;
                }
            };
        }
        return original;
    }

    @Override
    public CastingOutput wrap(RecipeScriptContext cx, Object from) {
        return CastingOutputWrapper.wrapCastingOutput(cx.cx(), from);
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, CastingOutput value) {
        if (value instanceof CastingOutput.TagOutput output) {
            builder.append(output.getTag().location());
        } else if (!isEmpty(value)) {
            builder.append(BuiltInRegistries.ITEM.getKey(value.getStack().getItem()).getPath());
        }
    }
}