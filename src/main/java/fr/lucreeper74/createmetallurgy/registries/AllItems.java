package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.tabs.AllCreativeTabs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.AllTags.forgeItemTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class AllItems {

    static {
        REGISTRATE.creativeModeTab(() -> AllCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final ItemEntry<Item> RAW_COBALT = taggedIngredient("raw_cobalt", forgeItemTag("raw_materials/cobalt"), forgeItemTag("raw_materials")),
            COBALT_INGOT = taggedIngredient("cobalt_ingot", forgeItemTag("ingots/cobalt"), Tags.Items.INGOTS),
            COBALT_PLATE = taggedIngredient("cobalt_plate", forgeItemTag("plates/cobalt"), forgeItemTag("plates")),
            COBALT_NUGGET = taggedIngredient("cobalt_nugget", forgeItemTag("nuggets/cobalt"), Tags.Items.NUGGETS);

    public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = taggedIngredient("graphite_blank_mold", forgeItemTag("graphite_molds/blank"), forgeItemTag("graphite_molds")),
            GRAPHITE_INGOT_MOLD = taggedIngredient("graphite_ingot_mold", forgeItemTag("graphite_molds/ingot"), forgeItemTag("graphite_molds")),
            GRAPHITE_NUGGET_MOLD = taggedIngredient("graphite_nugget_mold", forgeItemTag("graphite_molds/nugget"), forgeItemTag("graphite_molds")),
            GRAPHITE_PLATE_MOLD = taggedIngredient("graphite_plate_mold", forgeItemTag("graphite_molds/plate"), forgeItemTag("graphite_molds"));

    public static final ItemEntry<CombustibleItem> COKE = REGISTRATE.item("coke", CombustibleItem::new)
            .tag(forgeItemTag("coke"))
            .onRegister(i -> i.setBurnTime(2000))
            .register();
    public static final ItemEntry<Item> GRAPHITE = taggedIngredient("graphite", forgeItemTag("graphite"));
    public static final ItemEntry<Item> STEEL_INGOT = taggedIngredient("steel_ingot", forgeItemTag("ingots/steel"), Tags.Items.INGOTS);
    public static final ItemEntry<Item> STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new).register();

    //Shortcut

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    public static void register() {
    }
}