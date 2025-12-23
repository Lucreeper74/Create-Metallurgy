package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LadleFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
    public static final int LADLE_CAPACITY = 9000; // in mb

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;

    public LadleFluidHandler(@NotNull ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, holder);
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    public FluidStack getFluid() {
        return LadleItem.getFluidContents(container).getFluid();
    }

    protected void setFluid(FluidStack fluid) {
        FluidTank tank = new FluidTank(LADLE_CAPACITY);
        tank.setFluid(fluid);

        LadleItem.setFluidContents(container, tank);
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return LADLE_CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    public boolean isEmpty() {
        return getFluid().isEmpty();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource))
            return 0;

        FluidStack fluid = getFluid();
        if (action.simulate()) {
            if (fluid.isEmpty())
                return Math.min(getTankCapacity(0), resource.getAmount());
            if (!fluid.isFluidEqual(resource))
                return 0;
            return Math.min(getTankCapacity(0) - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(getTankCapacity(0), resource.getAmount()));
            onContentsChanged(fluid);
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
            return 0;
        int filled = getTankCapacity(0) - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else
            fluid.setAmount(getTankCapacity(0));
        if (filled > 0)
            onContentsChanged(fluid);
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fluid = getFluid();
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        FluidStack fluid = getFluid();
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged(fluid);
        }
        return stack;
    }

    protected void onContentsChanged(FluidStack newFluid) {
        LadleItem.setNextAddrs(container);
        setFluid(newFluid);
    }
}
