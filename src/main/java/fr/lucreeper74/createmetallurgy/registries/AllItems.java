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

    public static final ItemEntry<Item> RAW_WOLFRAMITE = taggedIngredient("raw_wolframite", forgeItemTag("raw_materials/wolframite"), forgeItemTag("raw_materials")),
            CRUSHED_RAW_WOLFRAMITE = taggedIngredient("crushed_raw_wolframite", forgeItemTag("crushed_raw_materials/wolframite")),
            TUNGSTEN_INGOT = taggedIngredient("tungsten_ingot", forgeItemTag("ingots/tungsten"), Tags.Items.INGOTS),
            TUNGSTEN_SHEET = taggedIngredient("tungsten_sheet", forgeItemTag("plates/tungsten"), forgeItemTag("plates")),
            TUNGSTEN_NUGGET = taggedIngredient("tungsten_nugget", forgeItemTag("nuggets/tungsten"), Tags.Items.NUGGETS);

    public static final ItemEntry<Item> DIRTY_WOLFRAMITE_DUST = taggedIngredient("dirty_wolframite_dust", forgeItemTag("dusts/dirty_wolframite"), forgeItemTag("dusts")),
            WOLFRAMITE_DUST = taggedIngredient("wolframite_dust", forgeItemTag("dusts/wolframite"), forgeItemTag("dusts")),
    // Gold
    DIRTY_GOLD_DUST = taggedIngredient("dirty_gold_dust", forgeItemTag("dusts/dirty_gold"), forgeItemTag("dusts")),
            GOLD_DUST = taggedIngredient("gold_dust", forgeItemTag("dusts/gold"), forgeItemTag("dusts")),
    // Iron
    DIRTY_IRON_DUST = taggedIngredient("dirty_iron_dust", forgeItemTag("dusts/dirty_iron"), forgeItemTag("dusts")),
            IRON_DUST = taggedIngredient("iron_dust", forgeItemTag("dusts/iron"), forgeItemTag("dusts")),
    // Copper
    DIRTY_COPPER_DUST = taggedIngredient("dirty_copper_dust", forgeItemTag("dusts/dirty_copper"), forgeItemTag("dusts")),
            COPPER_DUST = taggedIngredient("copper_dust", forgeItemTag("dusts/copper"), forgeItemTag("dusts")),
    // Zinc
    DIRTY_ZINC_DUST = taggedIngredient("dirty_zinc_dust", forgeItemTag("dusts/dirty_zinc"), forgeItemTag("dusts")),
            ZINC_DUST = taggedIngredient("zinc_dust", forgeItemTag("dusts/zinc"), forgeItemTag("dusts"));

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
    public static final ItemEntry<Item> TUNGSTEN_WIRE_SPOOL = REGISTRATE.item("tungsten_wire_spool", Item::new).register();

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