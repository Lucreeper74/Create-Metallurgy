package fr.lucreeper74.createmetallurgy.content.casting.basin;

import com.simibubi.create.AllShapes;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlockEntity;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlock;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CastingBasinBlock extends CastingBlock {

    public CastingBasinBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.BASIN_BLOCK_SHAPE;
    }

    @Override
    public Class<CastingBlockEntity> getBlockEntityClass() {
        return CastingBlockEntity.class;
    }

    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.CASTING_BASIN.get();
    }
}