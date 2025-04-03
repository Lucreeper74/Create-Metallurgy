package fr.lucreeper74.createmetallurgy.tabs;

import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.world.item.ItemStack;

public class MainCreativeTab extends CMCreativeModeTab {
    public MainCreativeTab() {
        super("main_group");
    }

    @Override
    public ItemStack makeIcon() {
        return CMItems.OBDURIUM_INGOT.asStack();
    }
}