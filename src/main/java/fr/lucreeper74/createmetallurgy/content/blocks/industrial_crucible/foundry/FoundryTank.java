package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoundryTank implements IFluidHandler {

    public List<FoundryTankSegment> segments;
    public int capacity;

    protected Runnable updateCallback;

    public FoundryTank(int capacity, Runnable updateCallback) {
        this.segments = new ArrayList<>();
        this.capacity = capacity;
        this.updateCallback = updateCallback;
    }

    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt, boolean clientPacket) {
        ListTag list = nbt.getList("Segments", Tag.TAG_COMPOUND);

        if (list.size() < segments.size())
            segments.subList(list.size(), segments.size()).clear();

        for (int i = 0; i < list.size(); i++) {
            if (i >= segments.size()) {
                FoundryTankSegment newSegment = new FoundryTankSegment();
                newSegment.deserializeNBT(lookupProvider, list.getCompound(i));
                segments.add(newSegment);
            } else {
                segments.get(i).deserializeNBT(lookupProvider, list.getCompound(i));
            }
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        ListTag tags = new ListTag();

        segments.forEach(ts -> tags.add(ts.serializeNBT(lookupProvider)));
        nbt.put("Segments", tags);
        return nbt;
    }

    public void tick() {
        for (FoundryTankSegment segment : segments)
            segment.tick();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getFillState() {
        return (float) getFillAmount() / getCapacity();
    }

    public int getFillAmount() {
        int filled = 0;
        for (FoundryTankSegment segment : segments)
            filled += segment.getFluid().getAmount();
        return filled;
    }

    public boolean isFluidValid(@NotNull FluidStack stack) {
        return true; // No conditions, every fluid accepted
    }

    @Override
    public int getTanks() {
        return segments.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank < 0 || tank >= segments.size())
            return FluidStack.EMPTY; // Out of bound
        return segments.get(tank).getFluid();
    }

    public List<FluidStack> getFluids() {
        List<FluidStack> fluids = new ArrayList<>();
        for (FoundryTankSegment segment : segments)
            if (!segment.getFluid().isEmpty())
                fluids.add(segment.getFluid());

        return fluids;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

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

        for (FoundryTankSegment segment : segments) {
            FluidStack fluid = segment.getFluid();
            if (FluidStack.isSameFluidSameComponents(fluid, resource)) {
                fluid.grow(filled);
                onContentChanged();
                return filled;
            }
        }

        // If here, no fluid matching

        resource = resource.copy(); // To be secure
        resource.setAmount(filled);
        segments.add(new FoundryTankSegment(resource));
        onContentChanged();
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        for (FoundryTankSegment segment : segments) {
            FluidStack fluid = segment.getFluid();
            if (FluidStack.isSameFluidSameComponents(fluid, resource))
                return drain(fluid, resource.getAmount(), action);
        }

        // No fluid matching
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (segments.isEmpty())
            return FluidStack.EMPTY;

        // Drain the first fluid if no fluid specified
        return drain(segments.getFirst().getFluid(), maxDrain, action);
    }

    public FluidStack drain(FluidStack resource, int maxDrain, FluidAction action) {
        int drained = maxDrain;

        if (resource.getAmount() < drained)
            drained = resource.getAmount();

        FluidStack stack = resource.copyWithAmount(drained);
        if (action.execute() && drained > 0) {
            resource.shrink(drained);

            if (resource.isEmpty())
                findSegmentAndRemove(resource);

            onContentChanged();
        }
        return stack;
    }

    protected void findSegmentAndRemove(FluidStack fluid) {
        segments.removeIf(segment -> FluidStack.isSameFluidSameComponents(segment.getFluid(), fluid));
    }

    protected void onContentChanged() {
        for (FoundryTankSegment segment : segments)
            segment.onFluidStackChanged();
        updateCallback.run();
    }

    public boolean isEmpty() {
        for (FoundryTankSegment segment : segments)
            if (!segment.isEmpty())
                return false;
        return true;
    }

    public class FoundryTankSegment {
        protected FluidStack fluid;

        // For rendering purposes only :
        protected LerpedFloat fluidLevel;

        public FoundryTankSegment() {
            this(FluidStack.EMPTY);
        }

        public FoundryTankSegment(FluidStack fluidStack) {
            this.fluid = fluidStack;
        }

        public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
            CompoundTag nbt = new CompoundTag();

            if (!fluid.isEmpty())
                nbt.put("Fluid", fluid.saveOptional(lookupProvider));
            return nbt;
        }

        public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
            fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid"));
            onFluidStackChanged();
        }

        public void tick() {
            if (fluidLevel != null)
                fluidLevel.tickChaser();
        }

        public void onFluidStackChanged() {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(getSegmentFillState());
            fluidLevel.chase(getSegmentFillState(), .8f, LerpedFloat.Chaser.EXP);
        }

        public float getSegmentFillState() {
            return fluid.getAmount() / (float) getCapacity();
        }

        public void setFluid(FluidStack fluid) {
            this.fluid = fluid;
            onFluidStackChanged();
        }

        public FluidStack getFluid() {
            return fluid;
        }

        public LerpedFloat getFluidLevel() {
            return fluidLevel;
        }

        public boolean isEmpty() {
            return fluid.isEmpty();
        }
    }
}