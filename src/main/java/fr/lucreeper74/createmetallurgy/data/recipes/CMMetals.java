package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.api.data.recipe.DatagenMod;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.recipe.Mods.*;
import static fr.lucreeper74.createmetallurgy.data.recipes.CMMods.*;

public enum CMMetals {
    // Simple metals
    IRON(() -> CMFluids.MOLTEN_IRON, VANILLA),
    COPPER(() -> CMFluids.MOLTEN_COPPER, VANILLA),
    GOLD(() -> CMFluids.MOLTEN_GOLD, VANILLA),
    NETHERITE(() -> CMFluids.MOLTEN_NETHERITE, VANILLA),

    ZINC(() -> CMFluids.MOLTEN_ZINC, CREATE),
    BRASS(() -> CMFluids.MOLTEN_BRASS, CREATE),

    TUNGSTEN(() -> CMFluids.MOLTEN_TUNGSTEN, "Wolframite", CREATE_METALLURGY),
    OBDURIUM(() -> CMFluids.MOLTEN_OBDURIUM, CREATE_METALLURGY),
    STEEL(() -> CMFluids.MOLTEN_STEEL, CREATE_METALLURGY),

    // Modded Metals
    ALUMINUM(() -> CMFluids.MOLTEN_ALUMINUM, IE, TFMG),
    LEAD(() -> CMFluids.MOLTEN_LEAD, MEK, TH, IE, TFMG),
    NICKEL(() -> CMFluids.MOLTEN_NICKEL, IE, TH, TFMG),
    OSMIUM(() -> CMFluids.MOLTEN_OSMIUM, MEK),
    SILVER(() -> CMFluids.MOLTEN_SILVER, IE, TH),
    TIN(() -> CMFluids.MOLTEN_TIN, MEK, TH),
    LITHIUM(() -> CMFluids.MOLTEN_LITHIUM, TFMG),

    // Alloys
    INVAR(() -> CMFluids.MOLTEN_INVAR, TH),
    ELECTRUM(() -> CMFluids.MOLTEN_ELECTRUM, MEK, TH, CADDITION),
    BRONZE(() -> CMFluids.MOLTEN_BRONZE, MEK, TH),
    CONSTANTAN(() -> CMFluids.MOLTEN_CONSTANTAN, TH, IE),
    VOID_STEEL(() -> CMFluids.MOLTEN_VOID_STEEL, CUTILITIES),
    NECROMIUM(() -> CMFluids.MOLTEN_NECROMIUM, CAVERNS_N_CHASMS),

    ;

    private final String name;
    private final String raw_name;
    private final Set<DatagenMod> mods;
    private final Supplier<FluidEntry<ForgeFlowingFluid.Flowing>> fluidSup;

    public final ItemLikeTag ores;
    public final ItemLikeTag rawStorageBlocks;
    public final ItemLikeTag storageBlocks;

    CMMetals(Supplier<FluidEntry<ForgeFlowingFluid.Flowing>> fluidSup, DatagenMod... mods) {
        this(fluidSup, "", mods);
    }

    CMMetals(Supplier<FluidEntry<ForgeFlowingFluid.Flowing>> fluidSup, String raw_name, DatagenMod... mods) {
        this.name = CMLang.asId(name());
        this.raw_name = raw_name.isEmpty() ? name : raw_name;
        this.fluidSup = fluidSup;
        this.mods = mods.length == 0 ? Set.of() : Set.copyOf(Set.of(mods));

        this.ores = new ItemLikeTag("ores/" + this.name);
        this.rawStorageBlocks = new ItemLikeTag("storage_blocks/raw_" + this.name);
        this.storageBlocks = new ItemLikeTag("storage_blocks/" + this.name);
    }

    public String getName() {
        return name;
    }

    public String getRawName(boolean isDisplayName) {
        if (isDisplayName)
            return (raw_name.substring(0, 1).toUpperCase(Locale.ROOT)
                    + raw_name.substring(1));
        else
            return raw_name;
    }

    public Set<DatagenMod> getMods() {
        return mods;
    }

    public int getMeltingPoint() {
        return getFluid().getType().getTemperature();
    }

    public boolean isStandard() {
        return (mods.contains(VANILLA) || mods.contains(CREATE) || mods.contains(CREATE_METALLURGY));
    }

    public FluidEntry<ForgeFlowingFluid.Flowing> getFluid() {
        return fluidSup.get();
    }

    public TagKey<Item> getItemTag(ItemType type) {
        return itemTag(type.getID() + getName());
    }

    private static TagKey<Item> itemTag(String path) {
        // TODO: change forge to c in 1.21
        return TagKey.create(Registries.ITEM, new ResourceLocation("forge", path));
    }

    private static TagKey<Block> blockTag(String path) {
        // TODO: change forge to c in 1.21
        return TagKey.create(Registries.BLOCK, new ResourceLocation("forge", path));
    }

    public record ItemLikeTag(TagKey<Item> items, TagKey<Block> blocks) {
        private ItemLikeTag(String path) {
            this(itemTag(path), blockTag(path));
        }
    }

    public enum ItemType {
        // Pure (can be cast/crafted from ingots)
        INGOT(() -> CMItems.GRAPHITE_INGOT_MOLD, 90, 1f),
        NUGGET(() -> CMItems.GRAPHITE_NUGGET_MOLD, 10, .11f),
        PLATE(() -> CMItems.GRAPHITE_PLATE_MOLD, 90, 1f),
        DUST(90, .5f, false),
        WIRE(45, .4f, false),
        GEAR(() -> CMItems.GRAPHITE_GEAR_MOLD, 360, 4f),
        ROD(() -> CMItems.GRAPHITE_ROD_MOLD, 45, .5f),
        COIN(10, .11f, false),
        BLOCK(810, 8f),

        // Impure
        RAW_MATERIAL(90, 45, 1f, false),
        RAW_CRUSHED(90, 45, .80f, false),
        RAW_BLOCK(810, 405, 8f, false),
        DIRTY_DUST(90, 30, .75f, false),
        ;

        private final String name;
        private final Supplier<ItemLike> mold;
        private final int fluidAmount;
        private final int impurity;
        private final float durationFactor;
        private final boolean canBeCast;

        ItemType(int fluidAmount, float durationFactor) {
            this(null, fluidAmount, 0, durationFactor, true);
        }

        ItemType(Supplier<ItemLike> mold, int fluidAmount, float durationFactor) {
            this(mold, fluidAmount, 0, durationFactor, true);
        }

        ItemType(int fluidAmount, float durationFactor, boolean canBeCast) {
            this(null, fluidAmount, 0, durationFactor, canBeCast);
        }

        ItemType(int fluidAmount, int impurity, float durationFactor, boolean canBeCast) {
            this(null, fluidAmount, impurity, durationFactor, canBeCast);
        }

        ItemType(Supplier<ItemLike> mold, int fluidAmount, int impurity, float durationFactor, boolean canBeCast) {
            this.name = CMLang.asId(name());
            this.mold = mold;
            this.fluidAmount = fluidAmount;
            this.impurity = impurity;
            this.durationFactor = durationFactor;
            this.canBeCast = canBeCast;
        }

        public String getName() {
            return name;
        }

        public String getID() {
            return switch (this) {
                case BLOCK -> "storage_blocks/";
                case RAW_BLOCK -> "storage_blocks/raw_";
                default -> name + "s/";
            };
        }

        public boolean hasMold() {
            return mold != null;
        }

        public ItemLike getMold() {
            return mold.get();
        }

        public boolean canBeCast() {
            return canBeCast;
        }

        public int getFluidAmount() {
            return fluidAmount;
        }

        public boolean isImpure() {
            return impurity > 0;
        }

        public int getImpurity() {
            return impurity;
        }

        public float getDurationFactor() {
            return durationFactor;
        }

        public ItemLike getItem(TagKey<Item> tag) {
            Iterator<Item> items = ForgeRegistries.ITEMS.tags().getTag(tag).iterator();
            return items.hasNext() ? items.next() : null;
        }
    }
}
