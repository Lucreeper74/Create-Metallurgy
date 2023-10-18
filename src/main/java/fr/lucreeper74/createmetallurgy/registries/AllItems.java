package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.tabs.CreateMetallurgyTab;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class AllItems {

    private static final CreateRegistrate REGISTRATE = CreateMetallurgy.REGISTRATE.creativeModeTab(() -> CreateMetallurgyTab.MAIN_GROUP);

    public static final ItemEntry<Item> CITRINE_SHARD = REGISTRATE.item("citrine_shard", Item::new)
            .register();
    public static final ItemEntry<Item> POLISHED_CITRINE = REGISTRATE.item("polished_citrine", Item::new)
            .register();
    public static final ItemEntry<Item> RAW_CITRINE = REGISTRATE.item("raw_citrine", Item::new)
            .register();
    public static final ItemEntry<Item> STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new)
            .register();
    public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = REGISTRATE.item("graphite_blank_mold", Item::new)
            .register();
    public static final ItemEntry<Item> GRAPHITE_INGOT_MOLD = REGISTRATE.item("graphite_ingot_mold", Item::new)
            .register();
    public static final ItemEntry<Item> GRAPHITE = REGISTRATE.item("graphite", Item::new)
            .register();

    public static void register() {}
}