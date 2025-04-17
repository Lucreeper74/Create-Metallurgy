package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.MeltingInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FoundryRecipe extends ProcessingRecipe<SmartInventory> {

    protected int minHeat;
    protected int maxHeat;

    public FoundryRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(typeInfo, params);

        validate(typeInfo.getId());
    }

    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom recipe (" + recipeTypeId + ")";
        Logger logger = CreateMetallurgy.LOGGER;

        if (minHeat > maxHeat) {
            logger.warn(messageHeader + " specified a minimum heat value greater than the maximum value.");
        }
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    public static boolean bulkMatch(CrucibleBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?> processRecipe) {
            boolean matchItem = true;

            List<Ingredient> ingredients = processRecipe.getIngredients();
            if (!ingredients.isEmpty()) {
                List<Integer> toExclude = new ArrayList<>();

                Ingredients:
                for (Ingredient item : ingredients) {
                    MeltingInventory inv = be.foundry.getInventory();

                    for (int i = 0; i < inv.getSlots(); i++) {
                        if (toExclude.contains(i))
                            continue;

                        if (item.test(inv.getSlot(i).getStack())) {
                            toExclude.add(i);
                            continue Ingredients;
                        }
                    }

                    // No matching item
                    matchItem = false;
                }
            }
            return fluidMatch(be, recipe) && matchItem;
        }
        return false;
    }

    public static boolean fluidMatch(CrucibleBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?> processRecipe) {
            List<FluidIngredient> fluidIngredients = processRecipe.getFluidIngredients();
            if (!fluidIngredients.isEmpty()) {
                FluidIngredient:
                for (FluidIngredient fluidIngredient : fluidIngredients) {

                    for (FluidStack fluid : be.getTank().fluids) {
                        if (fluidIngredient.test(fluid) && fluidIngredient.getRequiredAmount() <= fluid.getAmount())
                            continue FluidIngredient;
                    }
                    // No matching fluid
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean matchSpecific(ItemStack stack, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?> processRecipe) {
            return processRecipe.getIngredients().get(0).test(stack);
        }
        return false;
    }

    public static boolean isEnoughHeated(CrucibleBlockEntity ladle, ProcessingRecipe<?> recipe) {
        if (recipe == null)
            return false;

        int currentHeat = ladle.foundry.getCurrentHeat();

        if (recipe instanceof FoundryRecipe foundryRecipe)
            return currentHeat >= foundryRecipe.getMinHeat() && currentHeat <= foundryRecipe.getMaxHeat();
        else
            return currentHeat >= getHeatRequirement(recipe);
    }

    public static int getHeatRequirement(ProcessingRecipe<?> recipe) {
        if (recipe == null)
            return 0;

        return switch (recipe.getRequiredHeat()) {
            case NONE -> 0;
            case HEATED -> 2;
            case SUPERHEATED -> 3;
        };
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 10;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 10;
    }

    @Override
    public boolean matches(SmartInventory pContainer, Level pLevel) {
        return false;
    }

    public int getMaxHeat() {
        return maxHeat;
    }

    public int getMinHeat() {
        return minHeat;
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        maxHeat = GsonHelper.getAsInt(json, "maxHeatRequirement", 50);
        minHeat = GsonHelper.getAsInt(json, "minHeatRequirement", -50);
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);
        json.addProperty("maxHeatRequirement", maxHeat);
        json.addProperty("minHeatRequirement", minHeat);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        maxHeat = buffer.readInt();
        minHeat = buffer.readInt();
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        buffer.writeInt(maxHeat);
        buffer.writeInt(minHeat);
    }
}