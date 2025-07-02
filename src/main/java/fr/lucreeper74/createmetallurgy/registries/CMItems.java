package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.content.items.FoundryUnitItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import java.util.Locale;

import static com.simibubi.create.AllTags.AllItemTags.*;
import static com.simibubi.create.AllTags.forgeItemTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;
import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags.*;
import static net.minecraftforge.common.Tags.Items.DUSTS;
import static net.minecraftforge.common.Tags.Items.RAW_MATERIALS;

@SuppressWarnings("unused")
public class CMItems {

    static {
        REGISTRATE.setCreativeTab(CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final ItemEntry<Item> TUNGSTEN_INGOT = taggedIngredientFireResistant("tungsten_ingot", forgeItemTag("ingots/tungsten"), Tags.Items.INGOTS),
            TUNGSTEN_SHEET = taggedIngredientFireResistant("tungsten_sheet", forgeItemTag("plates/tungsten"), PLATES.tag),
            TUNGSTEN_NUGGET = taggedIngredientFireResistant("tungsten_nugget", forgeItemTag("nuggets/tungsten"), Tags.Items.NUGGETS),
            TUNGSTEN_WIRE = taggedIngredientFireResistant("tungsten_wire", forgeItemTag("wires/tungsten"), WIRES.tag);

    public static final ItemEntry<Item> OBDURIUM_INGOT = taggedIngredientFireResistant("obdurium_ingot", forgeItemTag("ingots/obdurium"), Tags.Items.INGOTS),
            OBDURIUM_SHEET = taggedIngredientFireResistant("obdurium_sheet", forgeItemTag("plates/obdurium"), PLATES.tag);


    public static final ItemEntry<Item> RAW_WOLFRAMITE = taggedIngredient("raw_wolframite", forgeItemTag("raw_materials/tungsten"), RAW_MATERIALS),
            CRUSHED_RAW_WOLFRAMITE = taggedIngredient("crushed_raw_wolframite", CRUSHED_RAW_MATERIALS.tag),
            DIRTY_WOLFRAMITE_DUST = taggedIngredient("dirty_wolframite_dust", forgeItemTag("dirty_dusts/tungsten"), DIRTY_DUSTS.tag),
            WOLFRAMITE_DUST = taggedIngredient("wolframite_dust", forgeItemTag("dusts/tungsten"), DUSTS);

    public static final ItemEntry<Item> DIRTY_GOLD_DUST = taggedIngredient("dirty_gold_dust", forgeItemTag("dirty_dusts/gold"), DIRTY_DUSTS.tag),
            GOLD_DUST = taggedIngredient("gold_dust", forgeItemTag("dusts/gold"), DUSTS);

    public static final ItemEntry<Item> DIRTY_IRON_DUST = taggedIngredient("dirty_iron_dust", forgeItemTag("dirty_dusts/iron"), DIRTY_DUSTS.tag),
            IRON_DUST = taggedIngredient("iron_dust", forgeItemTag("dusts/iron"), DUSTS);

    public static final ItemEntry<Item> DIRTY_COPPER_DUST = taggedIngredient("dirty_copper_dust", forgeItemTag("dirty_dusts/copper"), DIRTY_DUSTS.tag),
            COPPER_DUST = taggedIngredient("copper_dust", forgeItemTag("dusts/copper"), DUSTS);

    public static final ItemEntry<Item> DIRTY_ZINC_DUST = taggedIngredient("dirty_zinc_dust", forgeItemTag("dirty_dusts/zinc"), DIRTY_DUSTS.tag),
            ZINC_DUST = taggedIngredient("zinc_dust", forgeItemTag("dusts/zinc"), DUSTS);

    public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = taggedIngredient("graphite_blank_mold", forgeItemTag("graphite_molds/blank"), GRAPHITE_MOLDS.tag),
            GRAPHITE_INGOT_MOLD = taggedIngredient("graphite_ingot_mold", forgeItemTag("graphite_molds/ingot"), GRAPHITE_MOLDS.tag),
            GRAPHITE_NUGGET_MOLD = taggedIngredient("graphite_nugget_mold", forgeItemTag("graphite_molds/nugget"), GRAPHITE_MOLDS.tag),
            GRAPHITE_PLATE_MOLD = taggedIngredient("graphite_plate_mold", forgeItemTag("graphite_molds/plate"), GRAPHITE_MOLDS.tag),
            GRAPHITE_ROD_MOLD = taggedIngredient("graphite_rod_mold", forgeItemTag("graphite_molds/rod"), GRAPHITE_MOLDS.tag),
            GRAPHITE_GEAR_MOLD = taggedIngredient("graphite_gear_mold", forgeItemTag("graphite_molds/gear"), GRAPHITE_MOLDS.tag);

    public static final ItemEntry<Item> SLAG = taggedIngredient("slag", forgeItemTag("slag"));


    public static final ItemEntry<CombustibleItem> COKE = REGISTRATE.item("coke", CombustibleItem::new)
            .tag(forgeItemTag("coal_coke"))
            .onRegister(i -> i.setBurnTime(2000))
            .register();

    public static final ItemEntry<FoundryUnitItem> FOUNDRY_UNIT = REGISTRATE.item("foundry_unit", FoundryUnitItem::new)
            .register();


    public static final ItemEntry<Item> GRAPHITE = taggedIngredient("graphite", forgeItemTag("graphite")),
            STEEL_INGOT = taggedIngredient("steel_ingot", forgeItemTag("ingots/steel"), Tags.Items.INGOTS),
            STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new).register(),
            TUNGSTEN_WIRE_SPOOL = REGISTRATE.item("tungsten_wire_spool", Item::new).register(),
            SANDPAPER_BELT = REGISTRATE.item("sandpaper_belt", Item::new).register();

    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_INDUSTRIAL_CRUCIBLE = sequencedIngredient("incomplete_industrial_crucible", AllTags.AllItemTags.UPRIGHT_ON_BELT.tag);

    // Logistic
    static {
        for (PackageStyle style : LadleStyles.STYLES) {
            String size = "_" + style.width() + "x" + style.height();
            REGISTRATE.item(LadleStyles.getItemId(style).getPath(), p -> new LadleItem(p, LadleStyles.STYLES.get(0)))
                    .properties(p -> p.stacksTo(1))
                    .tag(PACKAGES.tag, LADLE.tag)
                    .model((c, p) ->
                            p.withExistingParent(c.getName(), p.modLoc("item/ladle/" + style.type() + size)))
                    .lang("Transfer " + style.type()
                            .substring(0, 1)
                            .toUpperCase(Locale.ROOT)
                            + style.type()
                            .substring(1))
                    .register();
        }
    }

    //Shortcut
    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredientFireResistant(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .properties(Item.Properties::fireResistant)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name, TagKey<Item>... tags) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .tag(tags)
                .register();
    }

    public static void register() {
    }
}