package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public abstract class CastingRecipeSerializer implements RecipeSerializer<CastingRecipe> {

    protected void writeToJson(JsonObject json, CastingRecipe recipe) {
        JsonArray jsonIngredients = new JsonArray();

        Ingredient ingredient = recipe.ingredient;
        if(!ingredient.isEmpty())
            jsonIngredients.add(ingredient.toJson());

        FluidIngredient fluidIngredient = recipe.fluidIngredient;
        if(fluidIngredient != FluidIngredient.EMPTY)
            jsonIngredients.add(recipe.fluidIngredient.serialize());

        json.add("ingredients", jsonIngredients);
        json.add("result", recipe.result.serialize());

        int processingDuration = recipe.getProcessingDuration();
        if (processingDuration > 0)
            json.addProperty("processingTime", processingDuration);

        if(recipe.moldConsumed)
            json.addProperty("mold_consumed", true);
    }

    @Override
    public CastingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        CastingRecipe recipe = createRecipe(recipeId);

        for (JsonElement je : GsonHelper.getAsJsonArray(json, "ingredients")) {
            if (FluidIngredient.isFluidIngredient(je))
                recipe.fluidIngredient =  FluidIngredient.deserialize(je);
            else
                recipe.ingredient = Ingredient.fromJson(je);
        }

        recipe.processingDuration = GsonHelper.getAsInt(json, "processingTime");
        recipe.moldConsumed = GsonHelper.getAsBoolean(json, "mold_consumed", false);

        JsonElement je =  GsonHelper.getAsJsonObject(json, "result");
        if(je.isJsonObject() && !GsonHelper.isValidNode(je.getAsJsonObject(), "fluid"))
            recipe.result = CastingOutput.deserialize(je);

        return recipe;
    }

    @Override
    public @Nullable CastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        CastingRecipe recipe = createRecipe(recipeId);
        recipe.ingredient = Ingredient.fromNetwork(buffer);
        recipe.fluidIngredient = FluidIngredient.read(buffer);
        recipe.processingDuration = buffer.readInt();
        recipe.moldConsumed = buffer.readBoolean();
        recipe.result = CastingOutput.read(buffer);

        return recipe;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CastingRecipe recipe) {
        recipe.ingredient.toNetwork(buffer);
        recipe.fluidIngredient.write(buffer);
        buffer.writeInt(recipe.processingDuration);
        buffer.writeBoolean(recipe.moldConsumed);
        recipe.result.write(buffer);
    }

    public final void write(JsonObject json, CastingRecipe recipe) {
        writeToJson(json, recipe);
    }

    public abstract CastingRecipe createRecipe(ResourceLocation id);


    public static class CastingTableRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe(ResourceLocation id) {
            return new CastingTableRecipe(id);
        }
    }

    public static class CastingBasinRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe(ResourceLocation id) {
            return new CastingBasinRecipe(id);
        }
    }
}