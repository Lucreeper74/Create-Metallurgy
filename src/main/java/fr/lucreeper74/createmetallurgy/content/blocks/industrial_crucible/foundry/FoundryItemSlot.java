package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FoundryItemSlot {
    private static final float SPEED_LIMIT = 3f;

    private final Supplier<CrucibleBlockEntity> controller;
    public ProcessingRecipe<?> currentRecipe;
    public boolean heating;
    public int processingTime;
    public int processDuration;

    private boolean contentsChanged;
    private final Runnable updateCallback;
    private ItemStack stack;

    public FoundryItemSlot(Supplier<CrucibleBlockEntity> controller, Runnable updateCallback) {
        this.controller = controller;
        this.processingTime = 0;
        this.updateCallback = updateCallback;
        this.stack = ItemStack.EMPTY;

        this.contentsChanged = true;
    }

    public CrucibleBlockEntity getController() {
        return controller.get();
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack newStack) {
        this.stack = newStack;
        contentsChanged = true;
    }

    public ItemStack removeStack() {
        ItemStack removedStack = getStack().copy();
        setStack(ItemStack.EMPTY);
        return removedStack;
    }

    private void reset() {
        processingTime = -1;
        processDuration = -1;
        currentRecipe = null;
        heating = false;
    }

    protected void tick() {
        if (!getController().getLevel().isClientSide) {
            if (contentsChanged) {
                contentsChanged = false;
                onContentChanged(true);
            }
        }

        if (canMelt())
            heatItem();
        else
            coolItem();
    }

    protected boolean canMelt() {
        if (!getController().getLevel().isClientSide) {
            boolean prevHeating = heating;
            heating = BulkMeltingRecipe.matches(getController(), currentRecipe, this);

            if (heating != prevHeating)
                contentsChanged = true;
        }

        return heating;
    }

    private void heatItem() {
        if (processingTime <= 0)
            tryMeltItem();
        else
            processingTime--;
    }

    private void coolItem() {
        if (processingTime < processDuration)
            processingTime++;
    }

    public void tryMeltItem() {
        if (currentRecipe == null)
            return;

        IFluidHandler fluidHandler = getController().getTank();

        for (FluidStack output : currentRecipe.getFluidResults()) {
            if (fluidHandler.fill(output.copy(), IFluidHandler.FluidAction.SIMULATE) >= output.getAmount()) {
                fluidHandler.fill(output.copy(), IFluidHandler.FluidAction.EXECUTE);
                setStack(ItemStack.EMPTY);
                reset();
            }
        }
    }

    private void onContentChanged(boolean notifyController) {
        updateMeltingRecipe();

        updateCallback.run();

        if (notifyController)
            getController().notifyUpdate();
    }

    public void updateMeltingRecipe() {
        if (currentRecipe != null)
            return;

        if (stack.isEmpty()) {
            reset();
            return;
        }

        ProcessingRecipe<?> recipe = getMatchingRecipe();
        if (recipe != null) {
            int duration = recipe.getProcessingDuration();

            processingTime = (int) (duration / getSpeedFactor(recipe));
            processDuration = processingTime;
            currentRecipe = recipe;
            heating = true;
        }
    }

    public float getSpeedFactor(ProcessingRecipe<?> recipe) {
        int minHeat;
        boolean heatingRecipe = true;
        if (recipe instanceof FoundryRecipe foundryRecipe) {
            minHeat = foundryRecipe.getMinHeat();
            heatingRecipe = (foundryRecipe.getMaxHeat() - foundryRecipe.getMinHeat()) / 2 >= 0;
        } else
            minHeat = FoundryRecipe.getHeatRequirement(recipe.getRequiredHeat());

        // 1+(speedLimit-1) (1-ℯ^(k (Tmin-x))) Paste this in math curve tracer
        return (float) (1f + (SPEED_LIMIT - 1f) * (1f - Math.exp((heatingRecipe ? .14f : -.14f)  * (minHeat - controller.get().foundryData.getCurrentHeat()))));
    }

    private ProcessingRecipe<?> getMatchingRecipe() {
        Level level = getController().getLevel();
        if (level == null)
            return null;

        Predicate<Recipe<?>> type = RecipeConditions.isOfType(CMRecipeTypes.BULK_MELTING.getType(), CMRecipeTypes.MELTING.getType());
        List<Recipe<?>> recipes = RecipeFinder.get(BulkMeltingCacheKey, level, type).stream()
                .filter(r -> BulkMeltingRecipe.matches(controller.get(), r, this))
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .toList();
        if (!recipes.isEmpty())
            return (ProcessingRecipe<?>) recipes.get(0);
        return null;
    }

    public void deserializeNBT(CompoundTag nbt, boolean clientPacket) {
        stack = ItemStack.of(nbt.getCompound("Stack"));
        processingTime = nbt.getInt("ProcessingTime");
        processDuration = nbt.getInt("ProcessDuration");
        heating = nbt.getBoolean("Heating");
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Stack", stack.save(new CompoundTag()));
        nbt.putInt("ProcessingTime", processingTime);
        nbt.putInt("ProcessDuration", processDuration);
        nbt.putBoolean("Heating", heating);
        return nbt;
    }

    private static final Object BulkMeltingCacheKey = new Object();
}
