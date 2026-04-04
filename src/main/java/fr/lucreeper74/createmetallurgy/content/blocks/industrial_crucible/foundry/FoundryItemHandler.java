package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoundryItemHandler implements IItemHandlerModifiable {

    // From net.minecraftforge.items.ItemStackHandler

    protected List<FoundryItemSlot> slots;

    public FoundryItemHandler() {
        this.slots = new ArrayList<>();
    }

    public void tick() {
        for (int i = 0; i < getSlots(); i++)
            getSlot(i).tick();
    }

    public void clear() {
        slots.clear();
    }

    public void addSlot(FoundryItemSlot slot) {
        slots.add(slot);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
    }

    public FoundryItemSlot getSlot(int slot) {
        validateSlotIndex(slot);
        return slots.get(slot);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return getSlot(slot).getStack().copy();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        stack.setCount(getSlotLimit(slot));
        this.slots.get(slot).setStack(stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        boolean canInsert = getStackInSlot(slot).isEmpty();
        if (canInsert && !simulate)
            setStackInSlot(slot, stack.copy());
        return canInsert ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1) : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack stackInSlot = getStackInSlot(slot);
        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate)
            return stackInSlot.copy();
        else {
            setStackInSlot(slot, ItemStack.EMPTY);
            return stackInSlot;
        }
    }

    public void notifyChangeOfContent() {
        for (FoundryItemSlot slot : slots)
            slot.notifyChangeOfContents();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1; // Always one for melting recipes
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true; // All item valid
    }
}
