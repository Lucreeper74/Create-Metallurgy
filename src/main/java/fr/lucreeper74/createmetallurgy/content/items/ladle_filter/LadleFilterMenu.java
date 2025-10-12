package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.tterrag.registrate.util.nullness.NonnullType;
import fr.lucreeper74.createmetallurgy.registries.CMMenuTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LadleFilterMenu extends AbstractFilterMenu {

    String address;
    FluidStack fluidFilter;
    int filledAmount;
    int comparator;

    public LadleFilterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public LadleFilterMenu(@NonnullType MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static LadleFilterMenu create(int id, Inventory inv, ItemStack stack) {
        return new LadleFilterMenu(CMMenuTypes.LADLE_FILTER.get(), id, inv, stack);
    }

    @Override
    protected int getPlayerInventoryXOffset() {
        return 40;
    }

    @Override
    protected int getPlayerInventoryYOffset() {
        return 101;
    }

    @Override
    protected void addFilterSlots() {
        this.addSlot(new SlotItemHandler(ghostInventory, 0, 16, 56));
    }

    @Override
    protected void init(Inventory inv, ItemStack contentHolderIn) {
        super.init(inv, contentHolderIn);
        ghostInventory.setStackInSlot(0, fluidFilter.getFluid().getBucket().getDefaultInstance());
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler();
    }

    @Override
    public void clearContents() {
        address = "";
        fluidFilter = FluidStack.EMPTY;
        filledAmount = -1;
        comparator = 0;
        ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        boolean defaults = !filterItem.hasTag();
        CompoundTag tag = filterItem.getOrCreateTag();

        filledAmount = defaults ? -1 : tag.getInt("FilledAmount");
        address =  defaults ? "*" : tag.getString("Address");
        comparator = defaults ? 0 : tag.getInt("Comparator");
        fluidFilter = FluidStack.loadFluidStackFromNBT(tag.getCompound("FluidFilter"));
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        super.saveData(filterItem);
        filterItem.getOrCreateTag()
                .putString("Address", address);
        filterItem.getOrCreateTag()
                .put("FluidFilter", fluidFilter.writeToNBT(new CompoundTag()));
        filterItem.getOrCreateTag()
                .putInt("FilledAmount", filledAmount);
        filterItem.getOrCreateTag()
                .putInt("Comparator", comparator);
    }
}