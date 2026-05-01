package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.processing.basin.BasinRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.faucet.FaucetBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.faucet.FaucetRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.FoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.FoundryMixerRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderVisual;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.basin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.table.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.FoundryMixerVisual;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.labeling_station.LabelingStationBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.LightBulbBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.LightBulbRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.labeling_station.LabelingStationRenderer;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMBlockEntityTypes {

    public static final BlockEntityEntry<FoundryBasinBlockEntity> FOUNDRY_BASIN = REGISTRATE
            .blockEntity("foundry_basin", FoundryBasinBlockEntity::new)
            .validBlocks(CMBlocks.FOUNDRY_BASIN_BLOCK)
            .renderer(() -> BasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingBasinBlockEntity> CASTING_BASIN = REGISTRATE
            .blockEntity("casting_basin", CastingBasinBlockEntity::new)
            .validBlocks(CMBlocks.CASTING_BASIN_BLOCK)
            .renderer(() -> CastingBlockRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingTableBlockEntity> CASTING_TABLE = REGISTRATE
            .blockEntity("casting_table", CastingTableBlockEntity::new)
            .validBlocks(CMBlocks.CASTING_TABLE_BLOCK)
            .renderer(() -> CastingBlockRenderer::new)
            .register();

    public static final BlockEntityEntry<FoundryLidBlockEntity> FOUNDRY_LID = REGISTRATE
            .blockEntity("foundry_lid", FoundryLidBlockEntity::new)
            .validBlocks(CMBlocks.FOUNDRY_LID_BLOCK)
            .register();

    public static final BlockEntityEntry<FoundryMixerBlockEntity> FOUNDRY_MIXER = REGISTRATE
            .blockEntity("foundry_mixer", FoundryMixerBlockEntity::new)
            .visual(() -> FoundryMixerVisual::new)
            .validBlocks(CMBlocks.FOUNDRY_MIXER_BLOCK)
            .renderer(() -> FoundryMixerRenderer::new)
            .register();

    public static final BlockEntityEntry<CrucibleBlockEntity> INDUSTRIAL_CRUCIBLE = REGISTRATE
            .blockEntity("industrial_crucible", CrucibleBlockEntity::new)
            .validBlocks(CMBlocks.INDUSTRIAL_CRUCIBLE)
            .renderer(() -> CrucibleRenderer::new)
            .register();

    public static final BlockEntityEntry<BeltGrinderBlockEntity> BELT_GRINDER = REGISTRATE
            .blockEntity("mechanical_belt_grinder", BeltGrinderBlockEntity::new)
            .visual(() -> BeltGrinderVisual::new)
            .validBlocks(CMBlocks.BELT_GRINDER_BLOCK)
            .renderer(() -> BeltGrinderRenderer::new)
            .register();

    public static final BlockEntityEntry<LightBulbBlockEntity> LIGHT_BULB = REGISTRATE
            .blockEntity("light_bulb", LightBulbBlockEntity::new)
            .validBlocks(CMBlocks.LIGHT_BULBS.toArray())
            .renderer(() -> LightBulbRenderer::new)
            .register();

    public static final BlockEntityEntry<FaucetBlockEntity> FAUCET = REGISTRATE
            .blockEntity("faucet", FaucetBlockEntity::new)
            .validBlocks(CMBlocks.FAUCET_BLOCK)
            .renderer(() -> FaucetRenderer::new)
            .register();

    public static final BlockEntityEntry<LabelingStationBlockEntity> LABELLING_STATION = REGISTRATE
            .blockEntity("labelling_station", LabelingStationBlockEntity::new)
            //.visual(() -> LabelingDepotVisual::new, true)
            .validBlocks(CMBlocks.LABELING_STATION_BLOCK)
            .renderer(() -> LabelingStationRenderer::new)
            .register();

//    public static final BlockEntityEntry<FoundryGaugeBlockEntity> FACTORY_GAUGE = REGISTRATE
//            .blockEntity("foundry_gauge", FoundryGaugeBlockEntity::new)
//            .validBlocks(CMBlocks.FOUNDRY_GAUGE_BLOCK)
//            .renderer(() -> FactoryPanelRenderer::new)
//            .register();

    public static void register() {
    }
}
