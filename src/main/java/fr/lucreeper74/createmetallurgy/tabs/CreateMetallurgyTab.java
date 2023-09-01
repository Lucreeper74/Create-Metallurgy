package fr.lucreeper74.createmetallurgy.tabs;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.AllItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreateMetallurgyTab {
    public static final CreativeModeTab MAIN_GROUP = new CreativeModeTab(CreateMetallurgy.MOD_ID + ".main_group")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(AllItems.CITRINE_SHARD.get());
        }
    };
}