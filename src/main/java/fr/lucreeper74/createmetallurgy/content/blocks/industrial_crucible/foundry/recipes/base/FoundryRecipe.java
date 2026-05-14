package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryItemHandler;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class FoundryRecipe<P extends FoundryRecipeParams> extends ProcessingRecipe<RecipeWrapper, P> {

    private final int minHeat;
    private final int maxHeat;

    public FoundryRecipe(CMRecipeTypes type, P params) {
        super(type, params);

        minHeat = params.minHeatRequirement;
        maxHeat = params.maxHeatRequirement;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        return false;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>(super.validate());

//        if (!fluidIngredients.isEmpty() && !ingredients.isEmpty())
//            errors.add("Recipe cannot have input items & fluids at the same time!");

        if (minHeat > maxHeat)
            errors.add("Recipe specified a minimum heat value greater than the maximum value.");

        return errors;
    }

    public static boolean match(CrucibleBlockEntity be, ProcessingRecipe<?, ?> processingRecipe) {
        boolean matchItem = true;

        List<Ingredient> ingredients = processingRecipe.getIngredients();
        if (!ingredients.isEmpty()) {
            List<Integer> toExclude = new ArrayList<>();

            Ingredients:
            for (Ingredient item : ingredients) {
                FoundryItemHandler inv = be.foundryData.getInputInv();

                for (int i = 0; i < inv.getSlots(); i++) {
                    if (toExclude.contains(i)) continue;

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

    public static boolean matchFluid(CrucibleBlockEntity be, ProcessingRecipe<?, ?> processRecipe) {
        List<SizedFluidIngredient> fluidIngredients = processRecipe.getFluidIngredients();
        if (!fluidIngredients.isEmpty()) {
            FluidIngredient:
            for (SizedFluidIngredient fluidIngredient : fluidIngredients) {

                for (FluidStack fluid : be.getTank().getFluids()) {
                    if (fluidIngredient.test(fluid))
                        continue FluidIngredient;
                }
                // No matching fluid
                return false;
            }
        }
        return true;
    }

    public static boolean matchSpecific(CrucibleBlockEntity be, ItemStack stack, ProcessingRecipe<?, ?> processRecipe) {
        if (!processRecipe.getIngredients().isEmpty())
            return processRecipe.getIngredients().getFirst().test(stack) && matchFluid(be, processRecipe);
        else return false;
    }

    public static boolean matchHeatCondition(CrucibleBlockEntity be, ProcessingRecipe<?, ?> recipe) {
        if (recipe == null) return false;

        int currentHeat = be.foundryData.getCurrentHeat();

        if (recipe instanceof FoundryRecipe<?> foundryRecipe)
            return currentHeat >= foundryRecipe.getMinHeat() && currentHeat <= foundryRecipe.getMaxHeat();
        else return currentHeat >= getHeatRequirement(recipe.getRequiredHeat());
    }

    public static int getHeatRequirement(HeatCondition heatCondition) {
        return switch (heatCondition) {
            case HEATED -> 2;
            case SUPERHEATED -> 3;
            default -> 0;
            // TODO: see if possible to register Tu value for HeatJS heat condition
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

    public int getMaxHeat() {
        return maxHeat;
    }

    public int getMinHeat() {
        return minHeat;
    }

    @FunctionalInterface
    public interface Factory<P extends FoundryRecipeParams, R extends FoundryRecipe<P>> extends ProcessingRecipe.Factory<P, R> {
        R create(P params);
    }
}