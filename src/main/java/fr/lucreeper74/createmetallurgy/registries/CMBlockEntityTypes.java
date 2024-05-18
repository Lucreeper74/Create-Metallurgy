package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerInstance;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerRenderer;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderBlockEntity;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderInstance;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin.CastingBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.glassedfoundrylid.GlassedFoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbBlockEntity;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.LightBulbRenderer;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMBlockEntityTypes {

    public static final BlockEntityEntry<FoundryBasinBlockEntity> FOUNDRY_BASIN = REGISTRATE
            .blockEntity("foundry_basin", FoundryBasinBlockEntity::new)
            .validBlocks(CMBlocks.FOUNDRY_BASIN_BLOCK)
            .renderer(() -> FoundryBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingBasinBlockEntity> CASTING_BASIN = REGISTRATE
            .blockEntity("casting_basin", CastingBasinBlockEntity::new)
            .validBlocks(CMBlocks.CASTING_BASIN_BLOCK)
            .renderer(() -> CastingBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingTableBlockEntity> CASTING_TABLE = REGISTRATE
            .blockEntity("casting_table", CastingTableBlockEntity::new)
            .validBlocks(CMBlocks.CASTING_TABLE_BLOCK)
            .renderer(() -> CastingTableRenderer::new)
            .register();

    public static final BlockEntityEntry<FoundryLidBlockEntity> FOUNDRY_LID = REGISTRATE
            .blockEntity("foundry_top", FoundryLidBlockEntity::new)
            .validBlocks(CMBlocks.FOUNDRY_LID_BLOCK)
            .renderer(() -> FoundryLidRenderer::new)
            .register();

    public static final BlockEntityEntry<GlassedFoundryLidBlockEntity> GLASSED_ALLOYER_TOP = REGISTRATE
            .blockEntity("glassed_foundry_top", GlassedFoundryLidBlockEntity::new)
            .validBlocks(CMBlocks.GLASSED_FOUNDRY_LID_BLOCK)
            .register();

    public static final BlockEntityEntry<FoundryMixerBlockEntity> FOUNDRY_MIXER = REGISTRATE
            .blockEntity("foundry_mixer", FoundryMixerBlockEntity::new)
            .instance(() -> FoundryMixerInstance::new)
            .validBlocks(CMBlocks.FOUNDRY_MIXER_BLOCK)
            .renderer(() -> FoundryMixerRenderer::new)
            .register();

    public static final BlockEntityEntry<BeltGrinderBlockEntity> BELT_GRINDER = REGISTRATE
            .blockEntity("mechanical_belt_grinder", BeltGrinderBlockEntity::new)
            .instance(() -> BeltGrinderInstance::new)
            .validBlocks(CMBlocks.BELT_GRINDER_BLOCK)
            .renderer(() -> BeltGrinderRenderer::new)
            .register();

    public static final BlockEntityEntry<LightBulbBlockEntity> LIGHT_BULB = REGISTRATE
            .blockEntity("light_bulb", LightBulbBlockEntity::new)
            .validBlocks(CMBlocks.LIGHT_BULBS.toArray())
            .renderer(() -> LightBulbRenderer::new)
            .register();

    public static void register() {}
}
