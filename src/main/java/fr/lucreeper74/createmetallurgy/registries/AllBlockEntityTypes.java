package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerInstance;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerRenderer;
import fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder.MechanicalBeltGrinderBlockEntity;
import fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder.MechanicalBeltGrinderInstance;
import fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder.MechanicalBeltGrinderRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.glassedfoundrylid.GlassedFoundryLidBlockEntity;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class AllBlockEntityTypes {

    public static final BlockEntityEntry<FoundryBasinBlockEntity> FOUNDRY_BASIN = REGISTRATE
            .blockEntity("foundry_basin", FoundryBasinBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_BASIN_BLOCK)
            .renderer(() -> FoundryBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingBasinBlockEntity> CASTING_BASIN = REGISTRATE
            .blockEntity("casting_basin", CastingBasinBlockEntity::new)
            .validBlocks(AllBlocks.CASTING_BASIN_BLOCK)
            .renderer(() -> CastingBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingTableBlockEntity> CASTING_TABLE = REGISTRATE
            .blockEntity("casting_table", CastingTableBlockEntity::new)
            .validBlocks(AllBlocks.CASTING_TABLE_BLOCK)
            .renderer(() -> CastingTableRenderer::new)
            .register();

    public static final BlockEntityEntry<FoundryLidBlockEntity> FOUNDRY_LID = REGISTRATE
            .blockEntity("foundry_top", FoundryLidBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_LID_BLOCK)
            .renderer(() -> FoundryLidRenderer::new)
            .register();

    public static final BlockEntityEntry<GlassedFoundryLidBlockEntity> GLASSED_ALLOYER_TOP = REGISTRATE
            .blockEntity("glassed_alloyer_top", GlassedFoundryLidBlockEntity::new)
            .validBlocks(AllBlocks.GLASSED_FOUNDRY_LID_BLOCK)
            .register();

    public static final BlockEntityEntry<FoundryMixerBlockEntity> FOUNDRY_MIXER = REGISTRATE
            .blockEntity("foundry_mixer", FoundryMixerBlockEntity::new)
            .instance(() -> FoundryMixerInstance::new)
            .validBlocks(AllBlocks.FOUNDRY_MIXER_BLOCK)
            .renderer(() -> FoundryMixerRenderer::new)
            .register();

    public static final BlockEntityEntry<MechanicalBeltGrinderBlockEntity> MECHANICAL_BELT_GRINDER = REGISTRATE
            .blockEntity("mechanical_belt_grinder", MechanicalBeltGrinderBlockEntity::new)
            .instance(() -> MechanicalBeltGrinderInstance::new)
            .validBlocks(AllBlocks.MECHANICAL_BELT_GRINDER_BLOCK)
            .renderer(() -> MechanicalBeltGrinderRenderer::new)
            .register();
    public static void register() {}
}
