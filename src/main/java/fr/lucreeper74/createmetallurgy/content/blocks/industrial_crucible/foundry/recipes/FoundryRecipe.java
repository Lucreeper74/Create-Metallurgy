package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity.MAX_SIZE;

public class FoundryRecipe extends ProcessingRecipe<SmartInventory> {

    protected int minHeat;
    protected int maxHeat;

    public FoundryRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(typeInfo, params);

        // validate(typeInfo.getId()); // Already called in readAdditional() !
    }

    private void validate(ResourceLocation typeInfoId) {
        String messageHeader = "Your custom recipe (" + typeInfoId + " named '" + getId().getPath() + "')";
        Logger logger = CreateMetallurgy.LOGGER;

        if (!fluidIngredients.isEmpty() && !ingredients.isEmpty())
            logger.warn("{} cannot have input items & fluids at the same time!", messageHeader);

        if (minHeat > maxHeat)
            logger.warn("{} specified a minimum heat value greater than the maximum value.", messageHeader);
    }

    public void apply(CrucibleBlockEntity be, ProcessingRecipe<?> processingRecipe) {

    }

    public static boolean match(CrucibleBlockEntity be, ProcessingRecipe<?> processingRecipe) {
        boolean matchItem = true;

        List<Ingredient> ingredients = processingRecipe.getIngredients();
        if (!ingredients.isEmpty()) {
            List<Integer> toExclude = new ArrayList<>();

                Ingredients:
                for (Ingredient item : ingredients) {
                    FoundryItemHandler inv = be.foundryData.getInputInv();

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
        return matchFluid(be, processingRecipe) && matchItem;
    }

    public static boolean matchFluid(CrucibleBlockEntity be, ProcessingRecipe<?> processRecipe) {
        List<FluidIngredient> fluidIngredients = processRecipe.getFluidIngredients();
        if (!fluidIngredients.isEmpty()) {
            FluidIngredient:
            for (FluidIngredient fluidIngredient : fluidIngredients) {

                for (FluidStack fluid : be.getTank().getFluids()) {
                    if (fluidIngredient.test(fluid) && fluidIngredient.getRequiredAmount() <= fluid.getAmount())
                        continue FluidIngredient;
                }
                // No matching fluid
                return false;
            }
        }
        return true;
    }

    public static boolean matchSpecific(CrucibleBlockEntity be, ItemStack stack, ProcessingRecipe<?> processRecipe) {
        if (!processRecipe.getIngredients().isEmpty())
            return processRecipe.getIngredients().get(0).test(stack) && matchFluid(be, processRecipe);
        else
            return false;
    }

    public static boolean matchHeatCondition(CrucibleBlockEntity be, ProcessingRecipe<?> recipe) {
        if (recipe == null)
            return false;

        int currentHeat = be.foundryData.getCurrentHeat();

        if (recipe instanceof FoundryRecipe foundryRecipe)
            return currentHeat >= foundryRecipe.getMinHeat() && currentHeat <= foundryRecipe.getMaxHeat();
        else
            return currentHeat >= getHeatRequirement(recipe.getRequiredHeat());
    }

    public static int getHeatRequirement(HeatCondition heatCondition) {
        return switch (heatCondition) {
            case NONE -> 0;
            case HEATED -> 2;
            case SUPERHEATED -> 3;
        };
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
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
        return 20;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 20;
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
        maxHeat = GsonHelper.getAsInt(json, "maxHeatRequirement", MAX_SIZE * MAX_SIZE * 2);
        minHeat = GsonHelper.getAsInt(json, "minHeatRequirement", -(MAX_SIZE * MAX_SIZE));

        validate(getTypeInfo().getId());
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