package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;

import com.mojang.datafixers.util.Pair;
import fr.lucreeper74.createmetallurgy.compat.jei.CMJeiTypes;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityIngredientHelper implements IIngredientHelper<DamagedEntityIngredient.EntityStack> {

    @Override
    public IIngredientType<DamagedEntityIngredient.EntityStack> getIngredientType() {
        return CMJeiTypes.ENTITY_STACK;
    }

    @Override
    public String getDisplayName(DamagedEntityIngredient.EntityStack type) {
        return type.type().getDescription().getString();
    }

    @SuppressWarnings("removal")
    @Override
    public String getUniqueId(DamagedEntityIngredient.EntityStack ingredient, UidContext context) {
        return getResourceLocation(ingredient).toString();
    }

    @Override
    public ResourceLocation getResourceLocation(DamagedEntityIngredient.EntityStack type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type.type());
    }

    @Override
    public DamagedEntityIngredient.EntityStack copyIngredient(DamagedEntityIngredient.EntityStack type) {
        return type;
    }

    @Override
    public String getErrorInfo(@Nullable DamagedEntityIngredient.EntityStack type) {
        if (type == null) {
            return "null";
        }
        return getResourceLocation(type).toString();
    }

    @Override
    public Optional<TagKey<?>> getTagKeyEquivalent(Collection<DamagedEntityIngredient.EntityStack> ingredients) {
        /* From JEI mezz.jei.common.util.TagUtil */

        if (ingredients.size() < 2)
            return Optional.empty();

        Set<EntityType<?>> values = ingredients.stream()
                .map(DamagedEntityIngredient.EntityStack::type)
                .collect(Collectors.toSet());

        return BuiltInRegistries.ENTITY_TYPE.getTags()
                .filter(entry -> {
                    HolderSet.Named<EntityType<?>> tag = entry.getSecond();

                    // Quick size check
                    if (tag.size() != values.size()) {
                        return false;
                    }

                    Set<EntityType<?>> tagValues = tag.stream()
                            .map(Holder::value)
                            .collect(Collectors.toSet());

                    return tagValues.equals(values);
                })
                .map(Pair::getFirst)
                .findFirst()
                .map(tag -> tag);
    }
}