package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CastingTableRecipe extends CastingRecipe {
    public CastingTableRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.get(CreateMetallurgy.genRL("casting_in_table"));
    }

    @Override
    public IRecipeTypeInfo getTypeInfo() {
        return CMRecipeTypes.CASTING_IN_TABLE;
    }
}