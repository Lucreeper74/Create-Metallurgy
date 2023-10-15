package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerInstance;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.castingbasin.CastingBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.castingtable.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.castingtable.CastingTableRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.foundrytop.CastingTopBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.glassedalloyertop.GlassedCastingTopBlockEntity;

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

    public static final BlockEntityEntry<CastingTopBlockEntity> FOUNDRY_TOP = REGISTRATE
            .blockEntity("foundry_top", CastingTopBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_TOP_BLOCK)
            .register();

    public static final BlockEntityEntry<GlassedCastingTopBlockEntity> GLASSED_ALLOYER_TOP = REGISTRATE
            .blockEntity("glassed_alloyer_top", GlassedCastingTopBlockEntity::new)
            .validBlocks(AllBlocks.GLASSED_ALLOYER_TOP_BLOCK)
            .register();

    public static final BlockEntityEntry<FoundryMixerBlockEntity> FOUNDRY_MIXER = REGISTRATE
            .blockEntity("foundry_mixer", FoundryMixerBlockEntity::new)
            .instance(() -> FoundryMixerInstance::new)
            .validBlocks(AllBlocks.FOUNDRY_MIXER_BLOCK)
            .renderer(() -> FoundryMixerRenderer::new)
            .register();

    public static void register() {}
}
