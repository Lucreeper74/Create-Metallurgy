package fr.lucreeper74.createmetallurgy.content.fluids;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class TagDependentBucketItem extends BucketItem {

    private TagKey<Item> tag;

    public TagDependentBucketItem(Fluid content, Properties properties, TagKey<Item> tag) {
        super(content, properties);
        this.tag = tag;
    }

    public boolean shouldHide() {
        for (Holder<Item> ignored : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
            return false; // at least 1 present
        }
        return true; // none present
    }
}
