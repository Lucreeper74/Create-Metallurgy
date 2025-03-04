package fr.lucreeper74.createmetallurgy.content.blocks.industrial_curcible.foundry;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_curcible.CurcibleBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class MeltingInventory implements IItemHandlerModifiable {

    private final CurcibleBlockEntity be;
    protected int firstLimitedSlot;
    private final MeltingSlot[] slots;

    public MeltingInventory(CurcibleBlockEntity be, int maxSize) {
        firstLimitedSlot = maxSize;
        this.slots = new MeltingSlot[maxSize];
        this.be = be;
    }

    public void setFirstLimitedSlot(int slot) {
        firstLimitedSlot = slot;
    }

    @Override
    public int getSlots() {
        return firstLimitedSlot;
    }

    public boolean isAccessible(int slot) {
        return slot >= 0 && slot < firstLimitedSlot;
    }

    public MeltingSlot getSlot(int slot) {
        if (slot >= firstLimitedSlot)
            throw new IndexOutOfBoundsException();

        if (slots[slot] == null)
            slots[slot] = new MeltingSlot(be);

        return slots[slot];
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (isAccessible(slot)) {
            MeltingSlot meltingSlot = getSlot(slot);
            if (meltingSlot != null)
                return meltingSlot.getStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty())
            getSlot(slot).setStack(ItemStack.EMPTY);
        else {
            if (stack.getCount() > 1)
                stack.setCount(1);
            getSlot(slot).setStack(stack);
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!isAccessible(slot))
            return stack;
        if (stack.isEmpty())
            return stack;

        boolean canInsert = getStackInSlot(slot).isEmpty();
        if (canInsert && !simulate)
            setStackInSlot(slot, stack.copy());
        return canInsert ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1) : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!isAccessible(slot))
            return ItemStack.EMPTY;

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

    @Override
    public int getSlotLimit(int slot) {
        return 1; // Always one for melting recipes
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }

    public void deserializeNBT(CompoundTag nbt) {
        firstLimitedSlot = nbt.getInt("LimitedSlot");

        ListTag list = nbt.getList("MeltingSlots", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag item = list.getCompound(i);
            if (item.contains("Slot", Tag.TAG_INT)) {
                int slot = item.getInt("Slot");
                if (isAccessible(slot))
                    getSlot(slot).deserializeNBT(item);
            }
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("LimitedSlot", firstLimitedSlot);

        ListTag list = new ListTag();
        for (int i = 0; i < slots.length; i++) {
            MeltingSlot meltingSlot = slots[i];
            if (meltingSlot != null) {
                CompoundTag slotNbt = meltingSlot.serializeNBT();
                slotNbt.putInt("Slot", i);
                list.add(slotNbt);
            }
        }
        if (!list.isEmpty()) {
            nbt.put("MeltingSlots", list);
        }
        return nbt;
    }
}
