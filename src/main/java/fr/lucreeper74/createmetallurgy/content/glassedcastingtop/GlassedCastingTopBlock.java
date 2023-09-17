package fr.lucreeper74.createmetallurgy.content.glassedcastingtop;

import fr.lucreeper74.createmetallurgy.content.castingtop.CastingTopBlock;
import fr.lucreeper74.createmetallurgy.content.castingtop.CastingTopBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.AllBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GlassedCastingTopBlock extends CastingTopBlock {
    public GlassedCastingTopBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntityType<? extends CastingTopBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.GLASSED_FOUNDRY_TOP.get();
    }
}
