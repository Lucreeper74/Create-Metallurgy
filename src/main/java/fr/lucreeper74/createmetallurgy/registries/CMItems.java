package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.tabs.CMCreativeTabs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.AllTags.AllItemTags.CRUSHED_RAW_MATERIALS;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import static com.simibubi.create.AllTags.forgeItemTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMItems {

    static {
        REGISTRATE.creativeModeTab(() -> CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final ItemEntry<Item> TUNGSTEN_INGOT = taggedIngredient("tungsten_ingot", forgeItemTag("ingots/tungsten"), Tags.Items.INGOTS),
            TUNGSTEN_SHEET = taggedIngredient("tungsten_sheet", forgeItemTag("plates/tungsten"),PLATES.tag),
            TUNGSTEN_NUGGET = taggedIngredient("tungsten_nugget", forgeItemTag("nuggets/tungsten"), Tags.Items.NUGGETS),
            TUNGSTEN_WIRE = taggedIngredient("tungsten_wire", forgeItemTag("wires/tungsten"), forgeItemTag("wires"));

    public static final ItemEntry<Item>  RAW_WOLFRAMITE = taggedIngredient("raw_wolframite", forgeItemTag("raw_materials/wolframite"), forgeItemTag("raw_materials")),
            CRUSHED_RAW_WOLFRAMITE = taggedIngredient("crushed_raw_wolframite", CRUSHED_RAW_MATERIALS.tag),
            DIRTY_WOLFRAMITE_DUST = taggedIngredient("dirty_wolframite_dust", forgeItemTag("dirty_dusts/dirty_wolframite"), forgeItemTag("dirty_dusts")),
            WOLFRAMITE_DUST = taggedIngredient("wolframite_dust", forgeItemTag("dusts/wolframite"), forgeItemTag("dusts"));

    public static final ItemEntry<Item> DIRTY_GOLD_DUST = taggedIngredient("dirty_gold_dust", forgeItemTag("dirty_dusts/dirty_gold"), forgeItemTag("dirty_dusts")),
            GOLD_DUST = taggedIngredient("gold_dust", forgeItemTag("dusts/gold"), forgeItemTag("dusts"));

    public static final ItemEntry<Item> DIRTY_IRON_DUST = taggedIngredient("dirty_iron_dust", forgeItemTag("dirty_dusts/dirty_iron"), forgeItemTag("dirty_dusts")),
            IRON_DUST = taggedIngredient("iron_dust", forgeItemTag("dusts/iron"), forgeItemTag("dusts"));

    public static final ItemEntry<Item> DIRTY_COPPER_DUST = taggedIngredient("dirty_copper_dust", forgeItemTag("dirty_dusts/dirty_copper"), forgeItemTag("dirty_dusts")),
            COPPER_DUST = taggedIngredient("copper_dust", forgeItemTag("dusts/copper"), forgeItemTag("dusts"));

    public static final ItemEntry<Item> DIRTY_ZINC_DUST = taggedIngredient("dirty_zinc_dust", forgeItemTag("dirty_dusts/dirty_zinc"), forgeItemTag("dirty_dusts")),
            ZINC_DUST = taggedIngredient("zinc_dust", forgeItemTag("dusts/zinc"), forgeItemTag("dusts"));

    public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = taggedIngredient("graphite_blank_mold", forgeItemTag("graphite_molds/blank"), forgeItemTag("graphite_molds")),
            GRAPHITE_INGOT_MOLD = taggedIngredient("graphite_ingot_mold", forgeItemTag("graphite_molds/ingot"), forgeItemTag("graphite_molds")),
            GRAPHITE_NUGGET_MOLD = taggedIngredient("graphite_nugget_mold", forgeItemTag("graphite_molds/nugget"), forgeItemTag("graphite_molds")),
            GRAPHITE_PLATE_MOLD = taggedIngredient("graphite_plate_mold", forgeItemTag("graphite_molds/plate"), forgeItemTag("graphite_molds")),
            GRAPHITE_ROD_MOLD = taggedIngredient("graphite_rod_mold", forgeItemTag("graphite_molds/rod"), forgeItemTag("graphite_molds")),
            GRAPHITE_GEAR_MOLD = taggedIngredient("graphite_gear_mold", forgeItemTag("graphite_molds/gear"), forgeItemTag("graphite_molds"));


    public static final ItemEntry<CombustibleItem> COKE = REGISTRATE.item("coke", CombustibleItem::new)
            .tag(forgeItemTag("coal_coke"))
            .onRegister(i -> i.setBurnTime(2000))
            .register();


    public static final ItemEntry<Item> GRAPHITE = taggedIngredient("graphite", forgeItemTag("graphite")),
            STEEL_INGOT = taggedIngredient("steel_ingot", forgeItemTag("ingots/steel"), Tags.Items.INGOTS),
            STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new).register(),
            TUNGSTEN_WIRE_SPOOL = REGISTRATE.item("tungsten_wire_spool", Item::new).register(),
            SANDPAPER_BELT = REGISTRATE.item("sandpaper_belt", Item::new).register();

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