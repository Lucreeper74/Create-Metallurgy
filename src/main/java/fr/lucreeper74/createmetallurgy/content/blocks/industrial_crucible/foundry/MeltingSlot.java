package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.function.Predicate;

public class MeltingSlot implements ContainerData {

    private ItemStack stack;
    private final CrucibleBlockEntity be;
    public ProcessingRecipe<?> currentRecipe;
    public int processingTime;
    public int processDuration;

    public MeltingSlot(CrucibleBlockEntity be) {
        this.be = be;
        processingTime = 0;
        stack = ItemStack.EMPTY;
    }

    public ItemStack getStack() {
        return stack;
    }

    private void reset() {
        processingTime = -1;
        processDuration = -1;
        currentRecipe = null;
    }

    private void startRecipe() {
        ProcessingRecipe<?> recipe = getMatchingRecipe();
        if (recipe != null) {
            int duration = recipe.getProcessingDuration();
            float speed = Mth.clamp((float) be.foundry.getCurrentHeat() / (FoundryRecipe.getHeatRequirement(recipe)), 1f, 3f);

            processingTime = (int) (duration / speed);
            processDuration = processingTime;
            currentRecipe = recipe;
        }
    }

    public void setStack(ItemStack newStack) {
        if (stack.isEmpty())
            reset();

        this.stack = newStack;
        startRecipe();
        be.notifyUpdate();
    }

    public boolean canMelt() {
        if (processingTime > 0 && FoundryRecipe.isEnoughHeated(be, currentRecipe)) {
            if (stack.isEmpty()) {
                reset();
                return false;
            }
            return true;
        } else {
            startRecipe();
        }
        return false;
    }

    public void heatItem() {
        processingTime--;

        if (processingTime <= 0)
            tryMeltItem();

        be.notifyUpdate();
    }

    public void coolItem() {
        if (currentRecipe == null)
            return;

        if (processingTime < currentRecipe.getProcessingDuration()) {
            processingTime++;
            be.notifyUpdate();
        }
    }

    public void tryMeltItem() {
        if (currentRecipe == null)
            return;

        IFluidHandler fluidHandler = be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .orElse(null);

        for (FluidStack output : currentRecipe.getFluidResults()) {
            if (fluidHandler.fill(output.copy(), IFluidHandler.FluidAction.SIMULATE) >= output.getAmount()) {
                fluidHandler.fill(output.copy(), IFluidHandler.FluidAction.EXECUTE);
                setStack(ItemStack.EMPTY);
            }
            reset();
        }
    }

    private ProcessingRecipe<?> getMatchingRecipe() {
        Level level = be.getLevel();
        if (level == null)
            return null;

        Predicate<Recipe<?>> type = RecipeConditions.isOfType(CMRecipeTypes.BULK_MELTING.getType(), CMRecipeTypes.MELTING.getType());
        List<Recipe<?>> recipes = RecipeFinder.get(BulkMeltingCacheKey, level, type).stream()
                .filter(r -> FoundryRecipe.match(be, r))
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .toList();
        if (!recipes.isEmpty())
            return (ProcessingRecipe<?>) recipes.get(0);
        return null;
    }

    @Override
    public int get(int pIndex) {
        return 0;
    }

    @Override
    public void set(int pIndex, int pValue) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    public void deserializeNBT(CompoundTag nbt) {
        stack = ItemStack.of(nbt);
        processingTime = nbt.getInt("processingTime");
        processDuration = nbt.getInt("processDuration");
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        stack.save(nbt);
        nbt.putInt("processingTime", processingTime);
        nbt.putInt("processDuration", processDuration);
        return nbt;
    }

    private static final Object BulkMeltingCacheKey = new Object();
}
