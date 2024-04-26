package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.basin.BasinGenerator;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderGenerator;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlock;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin.CastingBasinBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin.CastingBasinMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlock;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidGenerator;
import fr.lucreeper74.createmetallurgy.content.processing.glassedfoundrylid.GlassedFoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.processing.glassedfoundrylid.GlassedFoundryLidGenerator;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbBlock;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbGenerator;
import fr.lucreeper74.createmetallurgy.tabs.CMCreativeTabs;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
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
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TUNGSTEN_BLOCK = REGISTRATE
            .block("tungsten_block", Block::new)
            .initialProperties(() -> Blocks.EMERALD_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> WOLFRAMITE_ORE = REGISTRATE
            .block("wolframite_ore", Block::new)
            .initialProperties(() -> Blocks.COPPER_ORE)
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
            .simpleItem()
            .register();

    public static final BlockEntry<Block> STEEL_BLOCK = REGISTRATE
            .block("steel_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .simpleItem()
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
            .item()
            .transform(customItemModel("mechanical_belt_grinder", "item"))
            .register();

    public static final BlockEntry<LightBulbBlock> LIGHT_BULB = REGISTRATE
            .block("light_bulb", LightBulbBlock::new)
            .initialProperties(() -> Blocks.REDSTONE_LAMP)
            .properties(p -> p.color(MaterialColor.TERRACOTTA_BROWN)
                    .lightLevel(s -> s.getValue(LightBulbBlock.LEVEL)))
            .blockstate(new LightBulbGenerator()::generate)
            .addLayer(() -> RenderType::translucent)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .item()
            .transform(customItemModel("light_bulb", "item"))
            .register();

    public static void register() {}
}
