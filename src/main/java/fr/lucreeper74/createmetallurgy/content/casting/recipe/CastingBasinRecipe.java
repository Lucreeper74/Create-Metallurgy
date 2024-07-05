package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CastingBasinRecipe extends CastingRecipe {
    public CastingBasinRecipe(ResourceLocation id, Ingredient ingredient, FluidIngredient fluid, int processingTime, boolean moldConsumed, ProcessingOutput result) {
        super(id, ingredient, fluid, processingTime, moldConsumed, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BuiltInRegistries.RECIPE_SERIALIZER.get(CreateMetallurgy.genRL("casting_in_basin"));
    }

    @Override
    public RecipeType<?> getType() {
        return CMRecipeTypes.CASTING_IN_BASIN.getType();
    }
}