package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class LadleFilterItemStack extends FilterItemStack {

    public String AddressFilter;
    FluidStack fluidFilter;
    int filledAmount;
    int comparator;

    public LadleFilterItemStack(ItemStack filter) {
        super(filter);

        AddressFilter = LadleItem.getAddress(filter);
        fluidFilter = filter.get(CMDataComponents.LADLE_FILTER_FLUID);
        filledAmount = filter.getOrDefault(CMDataComponents.LADLE_FILTER_FLUID_AMOUNT, -1);
        comparator = filter.getOrDefault(CMDataComponents.LADLE_FILTER_COMPARATOR, 0);
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (super.test(world, stack, matchNBT))
            return true;

        if (LadleItem.isLadle(stack)) {
            boolean address_match = LadleItem.matchAddress(stack, AddressFilter) || AddressFilter.contentEquals("*");

            IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (fluidHandler == null)
                return true;

            boolean fluid_match = fluidFilter.isEmpty() || FluidStack.isSameFluidSameComponents(fluidFilter, fluidHandler.getFluidInTank(0));
            boolean filled_match = true;

            if (filledAmount >= 0) {
                int fluidAmount = fluidHandler.getFluidInTank(0).getAmount();

                filled_match = switch (comparator) {
                    case 1 -> fluidAmount > filledAmount;
                    case 2 -> fluidAmount >= filledAmount;
                    case 3 -> fluidAmount < filledAmount;
                    case 4 -> fluidAmount <= filledAmount;
                    default -> fluidAmount == filledAmount;
                };
            }

            return address_match && filled_match && fluid_match;
        }
        return false;
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        return false;
    }
}