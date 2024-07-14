package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.basin.BasinGenerator;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.UncontainableBlockItem;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.belt_grinder.BeltGrinderGenerator;
import fr.lucreeper74.createmetallurgy.content.foundry_mixer.FoundryMixerBlock;
import fr.lucreeper74.createmetallurgy.content.belt_grinder.BeltGrinderBlock;
import fr.lucreeper74.createmetallurgy.content.casting.basin.CastingBasinBlock;
import fr.lucreeper74.createmetallurgy.content.casting.basin.CastingBasinMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.casting.table.CastingTableBlock;
import fr.lucreeper74.createmetallurgy.content.casting.table.CastingTableMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinBlock;
import fr.lucreeper74.createmetallurgy.content.foundry_lid.FoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.foundry_lid.FoundryLidGenerator;
import fr.lucreeper74.createmetallurgy.content.glassed_foundry_lid.GlassedFoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.glassed_foundry_lid.GlassedFoundryLidGenerator;
import fr.lucreeper74.createmetallurgy.content.light_bulb.LightBulbBlock;
import fr.lucreeper74.createmetallurgy.tabs.CMCreativeTabs;
import fr.lucreeper74.createmetallurgy.utils.CMDyeHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.AllTags.forgeBlockTag;
import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static final BlockEntry<Block> RAW_WOLFRAMITE_BLOCK = REGISTRATE
            .block("raw_wolframite_block", Block::new)
            .initialProperties(() -> Blocks.RAW_COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem("storage_blocks/raw_wolframite"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> TUNGSTEN_BLOCK = REGISTRATE
            .block("tungsten_block", Block::new)
            .initialProperties(() -> Blocks.EMERALD_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem("storage_blocks/tungsten"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> WOLFRAMITE_ORE = REGISTRATE
            .block("wolframite_ore", Block::new)
            .initialProperties(() -> Blocks.COPPER_ORE)
            .loot((lt, b) -> lt.add(b,
                    RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
                            RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(CMItems.RAW_WOLFRAMITE.get())
                                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem("ores/wolframite"))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    public static final BlockEntry<Block> COKE_BLOCK = REGISTRATE
            .block("coke_block", Block::new)
            .initialProperties(() -> Blocks.COAL_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem("storage_blocks/coal_coke"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> STEEL_BLOCK = REGISTRATE
            .block("steel_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .transform(tagBlockAndItem("storage_blocks/steel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();


    public static final BlockEntry<Block> REFRACTORY_MORTAR = REGISTRATE
            .block("refractory_mortar", Block::new)
            .initialProperties(() -> Blocks.CLAY)
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .simpleItem()
            .register();

    public static final BlockEntry<FoundryBasinBlock> FOUNDRY_BASIN_BLOCK = REGISTRATE
            .block("foundry_basin", FoundryBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(new BasinGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new BasinMovementBehaviour()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<CastingBasinBlock> CASTING_BASIN_BLOCK = REGISTRATE
            .block("casting_basin", CastingBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new CastingBasinMovementBehaviour()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<CastingTableBlock> CASTING_TABLE_BLOCK = REGISTRATE
            .block("casting_table", CastingTableBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new CastingTableMovementBehaviour()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<FoundryLidBlock> FOUNDRY_LID_BLOCK = REGISTRATE
            .block("foundry_lid", FoundryLidBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(new FoundryLidGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<GlassedFoundryLidBlock> GLASSED_FOUNDRY_LID_BLOCK = REGISTRATE
            .block("glassed_foundry_lid", GlassedFoundryLidBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(new GlassedFoundryLidGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<FoundryMixerBlock> FOUNDRY_MIXER_BLOCK = REGISTRATE
            .block("foundry_mixer", FoundryMixerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.STONE))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel("foundry_mixer", "item"))
            .register();

    public static final BlockEntry<BeltGrinderBlock> BELT_GRINDER_BLOCK = REGISTRATE
            .block("mechanical_belt_grinder", BeltGrinderBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.STONE))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(new BeltGrinderGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setImpact(6.0))
            .transform(axeOrPickaxe())
            .onRegisterAfter(Registry.ITEM_REGISTRY, v -> ItemDescription.useKey(v, "block.createmetallurgy.mechanical_grinder"))
            .item()
            .transform(customItemModel("mechanical_belt_grinder", "item"))
            .register();

    public static final DyedBlockList<LightBulbBlock> LIGHT_BULBS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REGISTRATE.block(colorName + "_light_bulb", p -> new LightBulbBlock(p, color))
                .initialProperties(() -> Blocks.REDSTONE_LAMP)
                .properties(p -> p.sound(SoundType.GLASS).color(color.getMaterialColor())
                        .lightLevel(s -> s.getValue(LightBulbBlock.LEVEL)))
                .addLayer(() -> RenderType::translucent)
                .addLayer(() -> RenderType::cutoutMipped)
                .transform(axeOrPickaxe())
                .tag(forgeBlockTag("light_bulbs"))
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
                    ShapedRecipeBuilder.shaped(c.get())
                            .define('S', AllItems.IRON_SHEET.get())
                            .define('T', CMItems.TUNGSTEN_WIRE_SPOOL.get())
                            .define('G', CMDyeHelper.getGlassOfDye(color))
                            .pattern(" G ").pattern(" T ").pattern(" S ")
                            .unlockedBy("has_tungsten_wire_spool", RegistrateRecipeProvider.has(CMItems.TUNGSTEN_WIRE_SPOOL.get()))
                            .save(p, CreateMetallurgy.genRL("crafting/" + c.getName()));
                    ShapelessRecipeBuilder.shapeless(c.get())
                            .requires(color.getTag())
                            .requires(forgeItemTag("light_bulbs"))
                            .unlockedBy("has_light_bulb", RegistrateRecipeProvider.has(forgeItemTag("light_bulbs")))
                            .save(p, CreateMetallurgy.genRL("crafting/" + c.getName() + "_from_other_light_bulb"));
                })
                .onRegisterAfter(Registry.ITEM_REGISTRY, v -> ItemDescription.useKey(v, "block.createmetallurgy.light_bulb"))
                .item(UncontainableBlockItem::new)
                .tag(forgeItemTag("light_bulbs"))
                .model((c, p) -> p.withExistingParent(colorName + "_light_bulb", p.modLoc("block/light_bulb/item"))
                        .texture("0", p.modLoc("block/light_bulb/" + colorName)))
//                .transform(customItemModel(colorName + "_light_bulb", "item"))
                .build()
                .register();
    });

    public static void register() {
    }
}
