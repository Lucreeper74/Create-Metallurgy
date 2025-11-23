package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;
import java.util.function.Consumer;

public class FoundryRecipeBuilder<T extends FoundryRecipe> extends ProcessingRecipeBuilder<T> {

    public static final int DEFAULT_MIN_HEAT = -50;
    public static final int DEFAULT_MAX_HEAT = 50;

    /* Default heat requirements are the max/min possible */
    protected int minHeatRequirement = DEFAULT_MIN_HEAT;
    protected int maxHeatRequirement = DEFAULT_MAX_HEAT;

    public FoundryRecipeBuilder(ProcessingRecipeFactory<T> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    public FoundryRecipeBuilder<T> requiresMinHeat(int minHeat) {
        minHeatRequirement = minHeat;
        return this;
    }

    public FoundryRecipeBuilder<T> requiresMaxHeat(int maxHeat) {
        maxHeatRequirement = maxHeat;
        return this;
    }

    // Build Datagen
    @Override
    public T build() {
        T recipe = factory.create(params);
        recipe.minHeat = minHeatRequirement;
        recipe.maxHeat = maxHeatRequirement;
        return recipe;
    }

    @Override
    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new DataGenResult<>(build(), recipeConditions));
    }

    public static class DataGenResult<S extends FoundryRecipe> implements FinishedRecipe {

        private List<ICondition> recipeConditions;
        private ProcessingRecipeSerializer<S> serializer;
        private ResourceLocation id;
        private S recipe;

        @SuppressWarnings("unchecked")
        public DataGenResult(S recipe, List<ICondition> recipeConditions) {
            this.recipe = recipe;
            this.recipeConditions = recipeConditions;
            IRecipeTypeInfo recipeType = this.recipe.getTypeInfo();
            ResourceLocation typeId = recipeType.getId();

            if (!(recipeType.getSerializer() instanceof ProcessingRecipeSerializer))
                throw new IllegalStateException("Cannot datagen FoundryRecipe of type: " + typeId);

            this.id = new ResourceLocation(recipe.getId().getNamespace(),
                    typeId.getPath() + "/" + recipe.getId().getPath());
            this.serializer = (ProcessingRecipeSerializer<S>) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            serializer.write(json, recipe);
            if (recipeConditions.isEmpty())
                return;

            JsonArray conds = new JsonArray();
            recipeConditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
            json.add("conditions", conds);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }

    }
}
