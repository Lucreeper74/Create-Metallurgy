package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class LadleFilterItemStack extends FilterItemStack {

    public String AddressFilter;
    FluidStack fluidFilter;
    int filledAmount;
    int comparator;

    public LadleFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();
        CompoundTag tag = filter.getOrCreateTag();

        filledAmount = defaults ? -1 : tag.getInt("FilledAmount");
        AddressFilter = defaults ? "*" : tag.getString("Address");
        comparator = defaults ? 0 : tag.getInt("Comparator");
        fluidFilter = FluidStack.loadFluidStackFromNBT(tag.getCompound("FluidFilter"));
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (super.test(world, stack, matchNBT))
            return true;

        if (LadleItem.isLadle(stack)) {
            boolean address_match = LadleItem.matchAddress(stack, AddressFilter) || AddressFilter.contentEquals("*");
            boolean fluid_match = fluidFilter.isEmpty() || fluidFilter.isFluidEqual(LadleItem.getFluidContents(stack).getFluid());

            boolean filled_match = true;

            if (filledAmount >= 0) {
                int fluidAmount = LadleItem.getFluidAmount(stack);
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