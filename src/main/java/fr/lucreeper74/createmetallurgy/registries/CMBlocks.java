package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.processing.basin.BasinGenerator;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.UncontainableBlockItem;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderGenerator;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockMovementBehavior;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.basin.CastingBasinBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.table.CastingTableBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.faucet.FaucetBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.faucet.FaucetGenerator;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.FoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.FoundryLidGenerator;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.FoundryMixerBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockItem;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleGenerator;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleModel;
import fr.lucreeper74.createmetallurgy.content.blocks.labeling_station.LabelingStationBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.labeling_station.LabelingStationGenerator;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.LightBulbBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.tundish.TundishBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.tundish.TundishGenerator;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.registries.CMTags.CMBlockTags;
import fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags;
import fr.lucreeper74.createmetallurgy.utils.CMDyeHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.Tags;

import java.util.Map;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.MOD_ID;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMBlocks {

    static {
        REGISTRATE.setCreativeTab(CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final BlockEntry<Block> RAW_WOLFRAMITE_BLOCK = REGISTRATE
            .block("raw_wolframite_block", Block::new)
            .initialProperties(() -> Blocks.RAW_COPPER_BLOCK)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(metalTagBlockAndItem(CMMetals.TUNGSTEN.rawStorageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Raw Wolframite")
            .register();

    public static final BlockEntry<Block> TUNGSTEN_BLOCK = REGISTRATE
            .block("tungsten_block", Block::new)
            .initialProperties(() -> Blocks.EMERALD_BLOCK)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(metalTagBlockAndItem(CMMetals.TUNGSTEN.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(Item.Properties::fireResistant)
            .build()
            .lang("Block of Tungsten")
            .register();

    public static final BlockEntry<Block> OBDURIUM_BLOCK = REGISTRATE
            .block("obdurium_block", Block::new)
            .initialProperties(() -> Blocks.EMERALD_BLOCK)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(metalTagBlockAndItem(CMMetals.OBDURIUM.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(Item.Properties::fireResistant)
            .build()
            .lang("Block of Obdurium")
            .register();

    public static final BlockEntry<Block> WOLFRAMITE_ORE = REGISTRATE
            .block("wolframite_ore", Block::new)
            .initialProperties(() -> Blocks.NETHERRACK)
            .properties(p -> p.sound(SoundType.NETHERRACK))
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);

                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(CMItems.RAW_WOLFRAMITE.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem(Map.of(
                    CMMetals.TUNGSTEN.ores.blocks(), CMMetals.TUNGSTEN.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK
            )))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    public static final BlockEntry<Block> COKE_BLOCK = REGISTRATE
            .block("coke_block", Block::new)
            .initialProperties(() -> Blocks.COAL_BLOCK)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(TagGen.tagBlockAndItem(CMBlockTags.COKE_STORAGE_BLOCKS.tag, CMItemTags.COKE_STORAGE_BLOCKS.tag))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Coke")
            .register();

    public static final BlockEntry<Block> STEEL_BLOCK = REGISTRATE
            .block("steel_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(metalTagBlockAndItem(CMMetals.STEEL.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .lang("Block of Steel")
            .register();


    public static final BlockEntry<Block> REFRACTORY_MORTAR = REGISTRATE
            .block("refractory_mortar", Block::new)
            .initialProperties(() -> Blocks.CLAY)
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> SLAG_BLOCK = REGISTRATE
            .block("slag_block", Block::new)
            .initialProperties(() -> Blocks.BASALT)
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .simpleItem()
            .lang("Block of Slag")
            .register();

    public static final BlockEntry<FoundryBasinBlock> FOUNDRY_BASIN_BLOCK = REGISTRATE
            .block("foundry_basin", FoundryBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(new BasinGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new BasinMovementBehaviour()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<CastingBasinBlock> CASTING_BASIN_BLOCK = REGISTRATE
            .block("casting_basin", CastingBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new CastingBlockMovementBehavior()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<CastingTableBlock> CASTING_TABLE_BLOCK = REGISTRATE
            .block("casting_table", CastingTableBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new CastingBlockMovementBehavior()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<FoundryLidBlock> FOUNDRY_LID_BLOCK = REGISTRATE
            .block("foundry_lid", FoundryLidBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(new FoundryLidGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<FoundryMixerBlock> FOUNDRY_MIXER_BLOCK = REGISTRATE
            .block("foundry_mixer", FoundryMixerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, () -> 8.0))
            .item()
            .transform(customItemModel("foundry_mixer", "item"))
            .register();

    public static final BlockEntry<CrucibleBlock> INDUSTRIAL_CRUCIBLE = REGISTRATE
            .block("industrial_crucible", CrucibleBlock::new)
            .initialProperties(() -> Blocks.DEEPSLATE_BRICKS)
            .properties(p -> p.noOcclusion()
                    .isRedstoneConductor((p1, p2, p3) -> true)
                    .forceSolidOn())
            .transform(pickaxeOnly())
            .blockstate(new CrucibleGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> CrucibleModel::new))
            .transform(displaySource(CMDisplaySources.FOUNDRY_STATUS))
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
            .item(CrucibleBlockItem::new)
            .properties(Item.Properties::fireResistant)
            .model(AssetLookup.customBlockItemModel("_", "block_single"))
            .build()
            .register();

    public static final BlockEntry<BeltGrinderBlock> BELT_GRINDER_BLOCK = REGISTRATE
            .block("mechanical_belt_grinder", BeltGrinderBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(new BeltGrinderGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, () -> 6.0))
            .transform(axeOrPickaxe())
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block." + MOD_ID + ".mechanical_grinder"))
            .item()
            .transform(customItemModel("mechanical_belt_grinder", "item"))
            .register();

    public static final DyedBlockList<LightBulbBlock> LIGHT_BULBS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REGISTRATE.block(colorName + "_light_bulb", p -> new LightBulbBlock(p, color))
                .initialProperties(() -> Blocks.REDSTONE_LAMP)
                .properties(p -> p.sound(SoundType.GLASS).mapColor(color)
                        .lightLevel(s -> s.getValue(LightBulbBlock.LEVEL)))
                .addLayer(() -> RenderType::translucent)
                .transform(axeOrPickaxe())
                .tag(CMBlockTags.LIGHT_BULB.tag)
                .blockstate((c, p) -> p.getVariantBuilder(c.get())
                        .forAllStates(state -> {
                            Direction dir = state.getValue(LightBulbBlock.FACING);
                            String path = "block/light_bulb/";

                            return ConfiguredModel.builder()
                                    .modelFile(p.models()
                                            .withExistingParent(path + "tube/" + colorName, p.modLoc(path + "tube"))
                                            .texture("0", p.modLoc(path + colorName)))
                                    .modelFile(p.models()
                                            .withExistingParent(path + "tube_glow/" + colorName, p.modLoc(path + "tube_glow"))
                                            .texture("0", p.modLoc(path + colorName)))
                                    .modelFile(p.models()
                                            .withExistingParent(path + "block/" + colorName, p.modLoc(path + "block"))
                                            .texture("0", p.modLoc(path + colorName))
                                            .texture("particle", p.modLoc(path + colorName)))
                                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                                    .build();
                        }))
                .recipe((c, p) -> {
                    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, c.get())
                            .define('S', AllItems.IRON_SHEET.get())
                            .define('T', CMItems.TUNGSTEN_WIRE_SPOOL.get())
                            .define('G', CMDyeHelper.getGlassOfDye(color))
                            .pattern(" G ").pattern(" T ").pattern(" S ")
                            .unlockedBy("has_tungsten_wire_spool", RegistrateRecipeProvider.has(CMItems.TUNGSTEN_WIRE_SPOOL.get()))
                            .save(p, CreateMetallurgy.asResource("crafting/" + c.getName()));
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, c.get())
                            .requires(color.getTag())
                            .requires(CMItemTags.LIGHT_BULB.tag)
                            .unlockedBy("has_light_bulb", RegistrateRecipeProvider.has(CMItemTags.LIGHT_BULB.tag))
                            .save(p, CreateMetallurgy.asResource("crafting/light_bulbs/" + c.getName() + "_from_other_light_bulb"));
                })
                .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.createmetallurgy.light_bulb"))
                .item(UncontainableBlockItem::new)
                .tag(CMItemTags.LIGHT_BULB.tag)
                .model((c, p) -> p.withExistingParent(colorName + "_light_bulb", p.modLoc("block/light_bulb/item"))
                        .texture("0", p.modLoc("block/light_bulb/" + colorName)))
//              .transform(customItemModel(colorName + "_light_bulb", "item"))
                .build()
                .register();
    });

    public static final BlockEntry<FaucetBlock> FAUCET_BLOCK = REGISTRATE
            .block("faucet", FaucetBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(new FaucetGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block." + MOD_ID + ".faucet"))
            .item()
            .transform(customItemModel("faucet", "block"))
            .register();

    public static final BlockEntry<TundishBlock> TUNDISH_BLOCK = REGISTRATE.block("tundish", TundishBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::cutoutMipped)
            .blockstate(new TundishGenerator()::generate)
            .item()
            .transform(customItemModel("tundish", "single"))
            .register();

    public static final BlockEntry<LabelingStationBlock> LABELING_STATION_BLOCK = REGISTRATE
            .block("labeling_station", LabelingStationBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion())
            .properties(p -> p.isRedstoneConductor(($1, $2, $3) -> false))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(new LabelingStationGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block." + MOD_ID + ".labeling_station"))
            .item()
            .model(AssetLookup::customItemModel)
            .build()
            .register();

//    public static final BlockEntry<FoundryGaugeBlock> FOUNDRY_GAUGE_BLOCK =
//            REGISTRATE.block("foundry_gauge", FoundryGaugeBlock::new)
//                    .addLayer(() -> RenderType::cutoutMipped)
//                    .initialProperties(SharedProperties::copperMetal)
//                    .properties(BlockBehaviour.Properties::noOcclusion)
//                    .properties(BlockBehaviour.Properties::forceSolidOn)
//                    .transform(pickaxeOnly())
//                    .blockstate((c, p) -> p.horizontalFaceBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
//                    .onRegister(CreateRegistrate.blockModel(() -> FactoryPanelModel::new))
//                    //.transform(displaySource(AllDisplaySources.GAUGE_STATUS))
//                    .item(FactoryPanelBlockItem::new)
//                    .model(AssetLookup::customItemModel)
//                    .build()
//                    .register();

    public static void register() {
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> metalTagBlockAndItem(
            CMMetals.ItemLikeTag tag) {
        return tagBlockAndItem(Map.of(tag.blocks(), tag.items()));
    }
}
