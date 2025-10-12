package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;

import fr.lucreeper74.createmetallurgy.compat.jei.CMJeiTypes;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient.EntityStack> {
    @Override
    public IIngredientType<EntityIngredient.EntityStack> getIngredientType() {
        return CMJeiTypes.ENTITY_STACK;
    }

    @Override
    public String getDisplayName(EntityIngredient.EntityStack type) {
        return type.type().getDescription().getString();
    }

    @Override
    public String getUniqueId(EntityIngredient.EntityStack type, UidContext context) {
        return getResourceLocation(type).toString();
    }

    @Override
    public ResourceLocation getResourceLocation(EntityIngredient.EntityStack type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type.type());
    }

    @Override
    public EntityIngredient.EntityStack copyIngredient(EntityIngredient.EntityStack type) {
        return type;
    }

    @Override
    public String getErrorInfo(@Nullable EntityIngredient.EntityStack type) {
        if (type == null) {
            return "null";
        }
        return getResourceLocation(type).toString();
    }

    @Override
    public Optional<ResourceLocation> getTagEquivalent(Collection<EntityIngredient.EntityStack> ingredients) {
        /* From JEI mezz.jei.common.util.TagUtil */

        if (ingredients.size() < 2)
            return Optional.empty();

        List<? extends EntityType<?>> values = ingredients.stream()
                .map(EntityIngredient.EntityStack::type)
                .toList();

        return  BuiltInRegistries.ENTITY_TYPE.getTags()
                .filter(e -> {
                    HolderSet.Named<EntityType<?>> tag = e.getSecond();
                    int count = tag.size();
                    if (count == values.size()) {
                        return IntStream.range(0, count).allMatch(i -> {
                            EntityType<?> tagValue = tag.get(i).value();
                            EntityType<?> value = values.get(i);
                            return value.equals(tagValue);
                        });
                    }
                    return false;
                })
                .map(e -> e.getFirst().location())
                .findFirst();
    }
}