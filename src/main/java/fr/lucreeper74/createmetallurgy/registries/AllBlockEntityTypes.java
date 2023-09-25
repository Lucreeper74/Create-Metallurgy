package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.castingbasin.CastingBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.processing.castingtop.CastingTopBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.glassedcastingtop.GlassedCastingTopBlockEntity;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class AllBlockEntityTypes {

    public static final BlockEntityEntry<CastingBasinBlockEntity> FOUNDRY_BASIN = REGISTRATE
            .blockEntity("foundry_basin", CastingBasinBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_BASIN_BLOCK)
            .renderer(() -> CastingBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingTopBlockEntity> FOUNDRY_TOP = REGISTRATE
            .blockEntity("foundry_top", CastingTopBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_TOP_BLOCK)
            .register();

    public static final BlockEntityEntry<GlassedCastingTopBlockEntity> GLASSED_FOUNDRY_TOP = REGISTRATE
            .blockEntity("glassed_foundry_top", GlassedCastingTopBlockEntity::new)
            .validBlocks(AllBlocks.GLASSED_FOUNDRY_TOP_BLOCK)
            .register();

    public static final BlockEntityEntry<FoundryMixerBlockEntity> FOUNDRY_MIXER = REGISTRATE
            .blockEntity("foundry_mixer", FoundryMixerBlockEntity::new)
            .validBlocks(AllBlocks.FOUNDRY_MIXER_BLOCK)
            .register();

    public static void register() {}
}
