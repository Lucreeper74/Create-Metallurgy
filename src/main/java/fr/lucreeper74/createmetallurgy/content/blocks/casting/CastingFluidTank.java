package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class CastingFluidTank extends FluidTank {

    private final CastingBlockEntity be;
    protected LerpedFloat fluidLevel;

    protected Runnable updateCallback;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public CastingFluidTank(CastingBlockEntity be, Runnable updateCallback) {
        super(0);
        this.be = be;
        this.updateCallback = updateCallback;

        fluidLevel = LerpedFloat.linear()
                .startWithValue(0)
                .chase(0, .25f, LerpedFloat.Chaser.EXP);
    }

    public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt, boolean clientPacket) {
        super.readFromNBT(lookupProvider, nbt);
        setCapacity(nbt.getInt("Capacity"));
        fluidLevel.readNBT(nbt.getCompound("FluidLevel"), clientPacket);
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();

        super.writeToNBT(lookupProvider, nbt);
        nbt.putInt("Capacity", capacity);
        nbt.put("FluidLevel", fluidLevel.writeNBT());
        return nbt;
    }

    public void tick() {
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                be.sendData();
        }

        if (fluidLevel != null)
            fluidLevel.tickChaser();
    }

    public void sendDataLazily() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        be.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public void reset() {
        capacity = 0;
        fluid = FluidStack.EMPTY;
    }

    @Override
    protected void onContentsChanged() {
        if (!be.hasLevel())
            return;

        fluidLevel.chase(getFluidAmount() / (float) getCapacity(), .25f, LerpedFloat.Chaser.EXP);

        if (!be.getLevel().isClientSide())
            sendDataLazily();

        updateCallback.run();

        super.onContentsChanged();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource))
            return 0;

        int capacity = this.capacity;
        if (capacity == 0) {
            capacity = be.getRequirementFromFluid(resource.copy());
            if (capacity <= 0)
                return 0;
            if (action.execute()) {
                this.capacity = capacity;
            }
        }

        // Fill when empty
        if (fluid.isEmpty()) {
            if (action.execute()) {
                fluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
                onContentsChanged();
            }
            return fluid.getAmount();
        }
        // Safety (should never false)
        if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return 0;
        }
        // If full -> nothing
        int space = capacity - fluid.getAmount();
        if (space <= 0) {
            return 0;
        }
        // If enough space -> Fill
        int amount = resource.getAmount();
        if (amount < space) {
            if (action.execute()) {
                fluid.grow(amount);
                onContentsChanged();
            }
            return amount;
        } else {
            // If too much -> Fill to max
            if (action.execute()) {
                fluid.setAmount(capacity);
                onContentsChanged();
            }
            return space;
        }
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (be.running && be.processingTick > 0)
            return FluidStack.EMPTY; // Cannot drain while solidify

        int drained = Math.min(fluid.getAmount(), maxDrain);

        FluidStack stack = fluid.copyWithAmount(drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);

            if (fluid.isEmpty())
                // If no more fluid, reset casting process
                be.reset(); // Calling sendData()
            else
                onContentsChanged();
        }
        return stack;
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }
}