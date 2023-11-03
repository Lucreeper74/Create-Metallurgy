package fr.lucreeper74.createmetallurgy.tabs;

import fr.lucreeper74.createmetallurgy.registries.AllFluids;
import net.minecraft.world.item.ItemStack;

public class MainCreativeTab extends CreateMetallurgyCreativeModeTab {
    public MainCreativeTab() {
        super("main_group");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(AllFluids.MOLTEN_IRON.getBucket().get());
    }
}