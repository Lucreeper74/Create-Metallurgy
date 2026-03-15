package fr.lucreeper74.createmetallurgy.content.blocks.labeling_station;

import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class LadleItemHandler implements IItemHandlerModifiable {

    private LabelingStationBlockEntity blockEntity;
    private boolean canExtract;

    public LadleItemHandler(LabelingStationBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.canExtract = false;
    }
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot != 0)
            return;
        blockEntity.heldBox = stack;
    }

    public void allowExtract() {
        canExtract = true;
    }

    public void forbidExtract() {
        canExtract = false;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return blockEntity.heldBox;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!blockEntity.heldBox.isEmpty())
            return stack;
        if (!isItemValid(slot, stack))
            return stack;

        if (!simulate) {
            setStackInSlot(slot, stack.copy());
            blockEntity.boxArrived();
            blockEntity.notifyUpdate();
        }

        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack ladle = blockEntity.heldBox;
        if (blockEntity.animationTicks != 0 || !canExtract)
            return ItemStack.EMPTY;

        if (!simulate) {
            setStackInSlot(slot, ItemStack.EMPTY);
            forbidExtract();
            blockEntity.notifyUpdate();
        }
        return ladle;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return LadleItem.isLadle(stack);
    }
}
