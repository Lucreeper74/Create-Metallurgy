package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.tterrag.registrate.util.nullness.NonnullType;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMDataComponents;
import fr.lucreeper74.createmetallurgy.registries.CMMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LadleFilterMenu extends AbstractFilterMenu {

    String address;
    FluidStack fluidFilter;
    int filledAmount;
    int comparator;

    public LadleFilterMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
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
        address = "*";
        fluidFilter = FluidStack.EMPTY;
        filledAmount = -1;
        comparator = 0;
        ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);

        address = LadleItem.getAddress(filterItem);
        fluidFilter = filterItem.get(CMDataComponents.LADLE_FILTER_FLUID);
        filledAmount = filterItem.getOrDefault(CMDataComponents.LADLE_FILTER_FLUID_AMOUNT, -1);
        comparator = filterItem.getOrDefault(CMDataComponents.LADLE_FILTER_COMPARATOR, 0);
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        super.saveData(filterItem);
        if (address.isBlank())
            filterItem.set(AllDataComponents.PACKAGE_ADDRESS, "");
        else
            filterItem.set(AllDataComponents.PACKAGE_ADDRESS, address);

        filterItem.set(CMDataComponents.LADLE_FILTER_FLUID, fluidFilter);
        filterItem.set(CMDataComponents.LADLE_FILTER_FLUID_AMOUNT, filledAmount);
        filterItem.set(CMDataComponents.LADLE_FILTER_COMPARATOR, comparator);
    }
}