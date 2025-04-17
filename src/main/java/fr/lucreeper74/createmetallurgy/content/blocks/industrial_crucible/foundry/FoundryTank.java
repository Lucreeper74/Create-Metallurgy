package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FoundryTank implements IFluidHandler {

    public List<FluidStack> fluids;
    public int capacity;
    private final CrucibleBlockEntity be;
    protected Consumer<FluidStack> updateCallback;

    // For recipes
    private ProcessingRecipe<?> currentRecipe;
    public int processingTime;

    public FoundryTank(CrucibleBlockEntity be, int capacity, Consumer<FluidStack> updateCallback) {
        this.be = be;
        this.fluids = new ArrayList<>();
        this.capacity = capacity;
        this.updateCallback = updateCallback;

        processingTime = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getFillState() {
        return (float) getFillAmount() / capacity;
    }

    public int getFillAmount() {
        int filled = 0;
        for (FluidStack fluid : fluids)
            filled += fluid.getAmount();
        return filled;
    }

    public boolean isFluidValid(@NotNull FluidStack stack) {
        return true; // No conditions, every fluid accepted
    }

    @Override
    public int getTanks() {
        return fluids.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank < 0 || tank >= fluids.size())
            return FluidStack.EMPTY; // Out of bound
        return fluids.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    } // Never used ?

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack); // No conditions, every fluid accepted
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource))
            return 0;

        int filled = Math.min(resource.getAmount(), capacity - getFillAmount());

        if (action.simulate())
            return filled;

        for (FluidStack fluid : fluids) {
            if (fluid.isFluidEqual(resource)) {
                fluid.grow(filled);
                updateCallback.accept(fluid);
                return filled;
            }
        }

        // If here, no fluid matching

        resource = resource.copy(); // To be secure
        resource.setAmount(filled);
        fluids.add(resource);
        updateCallback.accept(resource);
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        for (FluidStack fluid : fluids)
            if (fluid.isFluidEqual(resource))
                return drain(fluid, resource.getAmount(), action);

        // No fluid matching
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (fluids.isEmpty())
            return FluidStack.EMPTY;

        // Drain the first fluid if no fluid specified
        return drain(fluids.get(0), maxDrain, action);
    }

    public FluidStack drain(FluidStack resource, int maxDrain, FluidAction action) {
        int drained = maxDrain;

        if (resource.getAmount() < drained)
            drained = resource.getAmount();

        FluidStack stack = new FluidStack(resource, drained);
        if (action.execute() && drained > 0) {
            resource.shrink(drained);

            if (resource.isEmpty())
                fluids.remove(resource);

            updateCallback.accept(resource);
        }
        return stack;
    }

    public boolean isEmpty() {
        for (FluidStack fluid : fluids)
            if (!fluid.isEmpty())
                return false;
        return true;
    }

    public void process() {
        ProcessingRecipe<?> recipe = getMatchingRecipe();
        if (recipe != null) {
            currentRecipe = recipe;

            if (FoundryRecipe.isEnoughHeated(be, currentRecipe)) {
                for (FluidIngredient fluidIngredient : recipe.getFluidIngredients())
                    for (int i = 0; i < fluids.size(); i++) {
                        FluidStack fluid = fluids.get(i);

                        if (fluidIngredient.test(fluid)) {
                            fluid.shrink(fluidIngredient.getRequiredAmount());
                            if (fluid.isEmpty())
                                fluids.remove(fluid);
                        }
                    }

                MeltingInventory inv = be.foundry.getInventory();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (int i = 0; i < inv.getSlots(); i++) {
                        MeltingSlot slot = inv.getSlot(i);
                        if (ingredient.test(slot.getStack())) {
                            slot.setStack(ItemStack.EMPTY);
                            break;
                        }
                    }
                }

                for (FluidStack output : recipe.getFluidResults())
                    if (fill(output.copy(), IFluidHandler.FluidAction.SIMULATE) >= output.getAmount())
                        fill(output.copy(), IFluidHandler.FluidAction.EXECUTE);
            }
            currentRecipe = null;
        }
    }

    private ProcessingRecipe<?> getMatchingRecipe() {
        Level level = be.getLevel();
        if (level == null)
            return null;

        Predicate<Recipe<?>> type = RecipeConditions.isOfType(CMRecipeTypes.ALLOYING.getType());
        List<Recipe<?>> recipes = RecipeFinder.get(BulkAlloyingCacheKey, level, type).stream()
                .filter(r -> FoundryRecipe.bulkMatch(be, r))
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .toList();
        if (!recipes.isEmpty())
            return (ProcessingRecipe<?>) recipes.get(0);
        return null;
    }

    public CompoundTag serializeNBT(CompoundTag nbt) {
        ListTag tags = new ListTag();
        for (FluidStack fluid : fluids)
            tags.add(fluid.writeToNBT(new CompoundTag()));
        nbt.put("Tanks", tags);

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        ListTag list = nbt.getList("Tanks", Tag.TAG_COMPOUND);
        fluids.clear();

        for (int i = 0; i < list.size(); i++) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(list.getCompound(i));
            if (fluids.size() <= i)
                fluids.add(fluid);
            else
                fluids.set(i, fluid);
        }
    }

    private static final Object BulkAlloyingCacheKey = new Object();
}