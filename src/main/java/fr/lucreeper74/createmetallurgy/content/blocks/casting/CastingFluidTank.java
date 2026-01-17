package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

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

    public FluidTank readFromNBT(CompoundTag nbt, boolean clientPacket) {
        setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
        setCapacity(nbt.getInt("capacity"));
        fluidLevel.readNBT(nbt.getCompound("level"), clientPacket);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
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

        if (!be.getLevel().isClientSide())
            sendDataLazily();

        be.notifyChangeOfContents();

        super.onContentsChanged();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }

        int capacity = this.capacity;
        if (capacity == 0) {
            capacity = be.checkCastingRecipe(resource);
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

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (be.running && be.processingTick > 0)
            return FluidStack.EMPTY; // Cannot drain while solidify

        int drained = Math.min(fluid.getAmount(), maxDrain);

        FluidStack stack = new FluidStack(fluid, drained);
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