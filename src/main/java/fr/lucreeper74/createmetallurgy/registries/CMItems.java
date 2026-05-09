package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.content.items.AttachmentItem.GaugeAttachmentItem;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterItem;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import static com.simibubi.create.AllTags.AllItemTags.*;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.MOD_ID;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;
import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

@SuppressWarnings("unused")
public class CMItems {

    static {
        REGISTRATE.setCreativeTab(CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final ItemEntry<Item> OBDURIUM_INGOT = taggedIngredientFireResistant("obdurium_ingot", CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
            OBDURIUM_SHEET = taggedIngredientFireResistant("obdurium_sheet", CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.PLATE), PLATES.tag);

    public static final ItemEntry<Item> RAW_WOLFRAMITE = taggedIngredientWithName("raw_tungsten", "Raw Wolframite", CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_MATERIAL), RAW_MATERIALS),
            CRUSHED_RAW_WOLFRAMITE = taggedIngredientWithName("crushed_raw_tungsten", "Crushed Raw Wolframite", CRUSHED_RAW_MATERIALS.tag);

    public static final ItemEntry<Item> TUNGSTEN_INGOT = taggedIngredientFireResistant("tungsten_ingot", CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
            TUNGSTEN_SHEET = taggedIngredientFireResistant("tungsten_sheet", CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.PLATE), PLATES.tag),
            TUNGSTEN_NUGGET = taggedIngredientFireResistant("tungsten_nugget", CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.NUGGET), NUGGETS),
            TUNGSTEN_WIRE = taggedIngredientFireResistant("tungsten_wire", CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.WIRE), WIRES.tag);

    public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = graphiteMold("graphite_blank_mold"),
            GRAPHITE_INGOT_MOLD = graphiteMold("graphite_ingot_mold"),
            GRAPHITE_NUGGET_MOLD = graphiteMold("graphite_nugget_mold"),
            GRAPHITE_PLATE_MOLD = graphiteMold("graphite_plate_mold"),
            GRAPHITE_ROD_MOLD = graphiteMold("graphite_rod_mold"),
            GRAPHITE_GEAR_MOLD = graphiteMold("graphite_gear_mold");

    // Metal Dust
    public static final ItemEntry<TagDependentIngredientItem> WOLFRAMITE_DUST = compatDust(CMMetals.TUNGSTEN, CMMetals.ItemType.DUST),
            GOLD_DUST = compatDust(CMMetals.GOLD, CMMetals.ItemType.DUST),
            IRON_DUST = compatDust(CMMetals.IRON, CMMetals.ItemType.DUST),
            COPPER_DUST = compatDust(CMMetals.COPPER, CMMetals.ItemType.DUST),
            ZINC_DUST = compatDust(CMMetals.ZINC, CMMetals.ItemType.DUST);

    // Metal Dirty Dust
    public static final ItemEntry<TagDependentIngredientItem> DIRTY_WOLFRAMITE_DUST = compatDust(CMMetals.TUNGSTEN, CMMetals.ItemType.DIRTY_DUST),
            DIRTY_GOLD_DUST = compatDust(CMMetals.GOLD, CMMetals.ItemType.DIRTY_DUST),
            DIRTY_IRON_DUST = compatDust(CMMetals.IRON, CMMetals.ItemType.DIRTY_DUST),
            DIRTY_COPPER_DUST = compatDust(CMMetals.COPPER, CMMetals.ItemType.DIRTY_DUST),
            DIRTY_ZINC_DUST = compatDust(CMMetals.ZINC, CMMetals.ItemType.DIRTY_DUST);


    public static final ItemEntry<Item> SLAG = taggedIngredient("slag", CMTags.CMItemTags.SLAG.tag);


    public static final ItemEntry<Item> COKE = REGISTRATE.item("coke", Item::new)
            .tag(COAL_COKE.tag)
            .burnTime(2000)
            .register();

    public static final ItemEntry<GaugeAttachmentItem> GAUGE_ATTACHMENT = REGISTRATE.item("gauge_attachment", GaugeAttachmentItem::new)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "item." + MOD_ID + ".gauge_attachment"))
            .register();
    //public static final ItemEntry<ItemPortAttachmentItem> ITEM_PORT_ATTACHMENT = REGISTRATE.item("item_port_attachment", ItemPortAttachmentItem::new).register();
    //public static final ItemEntry<FluidPortAttachmentItem> FLUID_PORT_ATTACHMENT = REGISTRATE.item("fluid_port_attachment", FluidPortAttachmentItem::new).register();


    public static final ItemEntry<LadleFilterItem> LADLE_FILTER = REGISTRATE.item("ladle_filter", LadleFilterItem::new)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "item." + MOD_ID + ".ladle_filter"))
            .register();

    public static final ItemEntry<Item> GRAPHITE = taggedIngredient("graphite", CMTags.CMItemTags.GRAPHITE.tag),
            STEEL_INGOT = taggedIngredient("steel_ingot", CMMetals.STEEL.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
            STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new).register(),
            TUNGSTEN_WIRE_SPOOL = REGISTRATE.item("tungsten_wire_spool", Item::new).register(),
            SANDPAPER_BELT = REGISTRATE.item("sandpaper_belt", Item::new).register(),
            REFRACTORY_MORTAR_BALL = REGISTRATE.item("refractory_mortar_ball", Item::new).register();

    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_INDUSTRIAL_CRUCIBLE = sequencedIngredient("incomplete_industrial_crucible", UPRIGHT_ON_BELT.tag),
            INCOMPLETE_LADLE_FRAME = sequencedIngredient("incomplete_ladle_frame", UPRIGHT_ON_BELT.tag);

    static {
        boolean rareCreated = false;
        boolean normalCreated = false;
        for (PackageStyle style : LadleStyles.LADLES_STYLES) {
            ItemBuilder<LadleItem, CreateRegistrate> ladleItem = REGISTRATE.item(LadleStyles.getItemId(style).getPath(), p -> new LadleItem(p, style))
                    .properties(p -> p.stacksTo(1))
                    .tag(PACKAGES.tag, LADLE.tag, NOT_UPRIGHT_ON_BELT.tag)
                    .model((c, p) -> {
                        if (style.rare())
                            p.withExistingParent(c.getName(), p.modLoc("item/ladle/custom"))
                                    .texture("2", p.modLoc("item/ladle/community/" + style.type()))
                                    .texture("particle", p.modLoc("item/ladle/community/" + style.type()));
                        else
                            p.withExistingParent(c.getName(), p.modLoc("item/ladle/" + style.type()));
                    })
                    .lang((style.rare() ? "Rare " : "") + "Transfer Ladle")
                    .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "item." + MOD_ID + ".ladle"));
            if (rareCreated && style.rare() || normalCreated && !style.rare())
                ladleItem.setData(ProviderType.LANG, NonNullBiConsumer.noop());

            rareCreated |= style.rare();
            normalCreated |= !style.rare();
            ladleItem.register();
        }
    }

    //Shortcuts
    private static ItemEntry<TagDependentIngredientItem> compatDust(CMMetals metal, CMMetals.ItemType dustType) {
        if (!dustType.equals(CMMetals.ItemType.DIRTY_DUST) && !dustType.equals(CMMetals.ItemType.DUST))
            return null;

        ItemBuilder<TagDependentIngredientItem, CreateRegistrate> itemEntry = REGISTRATE
                .item((dustType.equals(CMMetals.ItemType.DIRTY_DUST) ? "dirty_" : "") + metal.getName() + "_dust",
                        props -> new TagDependentIngredientItem(props, metal.getItemTag(dustType)))
                .tag(metal.getItemTag(dustType));

        switch (dustType) {
            case DIRTY_DUST -> itemEntry.tag(DIRTY_DUSTS.tag).lang("Dirty " + metal.getRawName(true) + " Dust");
            case DUST -> itemEntry.tag(DUSTS).lang(metal.getRawName(true) + " Dust");
        }

        return itemEntry.register();
    }

    private static ItemEntry<Item> graphiteMold(String id) {
        return REGISTRATE.item(id, Item::new)
                .tag(GRAPHITE_MOLDS.tag)
                .recipe((c, p) ->
                        p.stonecutting(DataIngredient.tag(GRAPHITE_MOLDS.tag), RecipeCategory.MISC, c))
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String id, TagKey<Item>... tags) {
        return REGISTRATE.item(id, Item::new)
                .tag(tags)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredientWithName(String id, String name, TagKey<Item>... tags) {
        return REGISTRATE.item(id, Item::new)
                .tag(tags)
                .lang(name)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredientFireResistant(String id, TagKey<Item>... tags) {
        return REGISTRATE.item(id, Item::new)
                .tag(tags)
                .properties(Item.Properties::fireResistant)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String id) {
        return REGISTRATE.item(id, SequencedAssemblyItem::new)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String id, TagKey<Item>... tags) {
        return REGISTRATE.item(id, SequencedAssemblyItem::new)
                .tag(tags)
                .register();
    }

    public static void register() {
    }
}