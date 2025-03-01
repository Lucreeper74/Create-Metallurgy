package fr.lucreeper74.createmetallurgy.content.fluids;

import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public class TagDependentBucketItem extends BucketItem {

    private TagKey<Item> tag;
    public TagDependentBucketItem(java.util.function.Supplier<? extends Fluid> supplier, Item.Properties builder, TagKey<Item> tag) {
        super(supplier, builder);
        this.tag = tag;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (!shouldHide())
            super.fillItemCategory(tab, list);
    }

    public boolean shouldHide() {
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        return !tagManager.isKnownTagName(tag) || tagManager.getTag(tag).isEmpty();
    }
}
