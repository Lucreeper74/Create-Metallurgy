package fr.lucreeper74.createmetallurgy.content.castingbasin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.registries.AllBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CastingBasinBlock extends BasinBlock implements IBE<BasinBlockEntity>, IWrenchable {
    public CastingBasinBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }
    @Override
    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CASTING_BASIN.get();
    }
}
