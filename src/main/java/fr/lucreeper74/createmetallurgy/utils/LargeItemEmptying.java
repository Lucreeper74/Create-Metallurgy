package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Optional;

public class LargeItemEmptying {
    public static Pair<FluidStack, ItemStack> emptyItem(Level level, ItemStack stack, int maxAmount, boolean simulate) {
        FluidStack resultingFluid = FluidStack.EMPTY;
        ItemStack resultingItem = ItemStack.EMPTY;

        if (PotionFluidHandler.isPotionItem(stack))
            return PotionFluidHandler.emptyPotion(stack, simulate);

        Optional<RecipeHolder<Recipe<SingleRecipeInput>>> recipe = AllRecipeTypes.EMPTYING.find(new SingleRecipeInput(stack), level);
        if (recipe.isPresent()) {
            EmptyingRecipe emptyingRecipe = (EmptyingRecipe) recipe.get().value();
            List<ItemStack> results = emptyingRecipe.rollResults(level.random);
            if (!simulate)
                stack.shrink(1);
            resultingItem = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
            resultingFluid = emptyingRecipe.getResultingFluid();
            return Pair.of(resultingFluid, resultingItem);
        }

        ItemStack split = stack.copy();
        split.setCount(1);
        IFluidHandlerItem capability = split.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return Pair.of(resultingFluid, resultingItem);
        resultingFluid = capability.drain(maxAmount, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        resultingItem = capability.getContainer()
                .copy();
        if (!simulate)
            stack.shrink(1);

        return Pair.of(resultingFluid, resultingItem);
    }
}