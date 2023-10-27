package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.basin.BasinGenerator;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlock;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableMovementBehaviour;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlock;
import fr.lucreeper74.createmetallurgy.content.processing.foundrytop.CastingTopBlock;
import fr.lucreeper74.createmetallurgy.content.processing.glassedalloyertop.GlassedCastingTopBlock;
import fr.lucreeper74.createmetallurgy.tabs.CreateMetallurgyTab;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

@SuppressWarnings("unused")
public class AllBlocks {
    private static final CreateRegistrate REGISTRATE = CreateMetallurgy.REGISTRATE.creativeModeTab(() -> CreateMetallurgyTab.MAIN_GROUP);

    public static final BlockEntry<Block> RAW_CITRINE_BLOCK = REGISTRATE
            .block("raw_citrine_block", Block::new)
            .initialProperties(() -> Blocks.RAW_COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> POLISHED_CITRINE_BLOCK = REGISTRATE
            .block("polished_citrine_block", Block::new)
            .initialProperties(() -> Blocks.EMERALD_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CITRINE_ORE = REGISTRATE
            .block("citrine_ore", Block::new)
            .initialProperties(() -> Blocks.COPPER_ORE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
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
            .blockstate(new BasinGenerator()::generate)
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
            .blockstate(new BasinGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new CastingTableMovementBehaviour()))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<CastingTopBlock> FOUNDRY_TOP_BLOCK = REGISTRATE
            .block("foundry_top", CastingTopBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<GlassedCastingTopBlock> GLASSED_ALLOYER_TOP_BLOCK = REGISTRATE
            .block("glassed_alloyer_top", GlassedCastingTopBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
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
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel("_", "block"))
            .register();
    public static void register() {}
}
