package fr.lucreeper74.createmetallurgy.content.casting;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CastingFluidTank extends FluidTank {

    private final CastingBlockEntity be;
    protected LerpedFloat fluidLevel;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public CastingFluidTank(CastingBlockEntity be) {
        super(0);
        this.be = be;
        fluidLevel = LerpedFloat.linear()
                .startWithValue(0)
                .chase(0, .25f, LerpedFloat.Chaser.EXP);
    }

    public FluidTank readFromNBT(CompoundTag nbt) {
        setFluid(FluidStack.loadFluidStackFromNBT(nbt));
        setCapacity(nbt.getInt("capacity"));
        fluidLevel.readNBT(nbt.getCompound("level"), true);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        fluid.writeToNBT(nbt);
        nbt.putInt("capacity", capacity);
        nbt.put("level", fluidLevel.writeNBT());
        return nbt;
    }

    public void tick() {
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                updateFluids();
        }
        LerpedFloat fluidLevel = getFluidLevel();
        if (fluidLevel != null)
            fluidLevel.tickChaser();
    }

    public void sendDataLazily() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        updateFluids();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    protected void updateFluids() {
        be.sendData();
        be.setChanged();
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
        if (!be.getLevel().isClientSide)
            sendDataLazily();
        super.onContentsChanged();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }

        int capacity = this.capacity;
        if (capacity == 0) {
            capacity = be.initProcess(resource, action);
            if (capacity <= 0)
                return 0;
            if (action.execute()) {
                this.capacity = capacity;
            }
        }

        // Fill when empty
        if (fluid.isEmpty()) {
            int amount = Math.min(capacity, resource.getAmount());
            if (action.execute()) {
                fluid = new FluidStack(resource, amount);
                onContentsChanged();
            }
            return amount;
        }
        // Safety (should never false)
        if (!fluid.isFluidEqual(resource)) {
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

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }
}