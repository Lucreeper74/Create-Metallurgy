package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.lucreeper74.createmetallurgy.content.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.castingbasin.CastingBasinRenderer;
import fr.lucreeper74.createmetallurgy.content.castingtop.CastingTopBlockEntity;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class AllBlockEntityTypes {

    public static final BlockEntityEntry<CastingBasinBlockEntity> CASTING_BASIN = REGISTRATE
            .blockEntity("casting_basin", CastingBasinBlockEntity::new)
            .validBlocks(AllBlocks.CASTING_BASIN_BLOCK)
            .renderer(() -> CastingBasinRenderer::new)
            .register();

    public static final BlockEntityEntry<CastingTopBlockEntity> CASTING_TOP = REGISTRATE
            .blockEntity("casting_top", CastingTopBlockEntity::new)
            .validBlocks(AllBlocks.CASTING_TOP_BLOCK)
            .register();
    public static void register() {}
}
