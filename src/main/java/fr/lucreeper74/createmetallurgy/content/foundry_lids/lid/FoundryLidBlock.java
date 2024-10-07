package fr.lucreeper74.createmetallurgy.content.foundry_lids.lid;

import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.content.foundry_lids.LidBlock;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FoundryLidBlock extends LidBlock implements IBE<FoundryLidBlockEntity> {

    public FoundryLidBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<FoundryLidBlockEntity> getBlockEntityClass() {
        return FoundryLidBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FoundryLidBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.FOUNDRY_LID.get();
    }
}