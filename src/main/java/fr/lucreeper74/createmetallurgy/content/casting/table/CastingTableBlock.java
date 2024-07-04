package fr.lucreeper74.createmetallurgy.content.casting.table;

import com.simibubi.create.AllShapes;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlock;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CastingTableBlock extends CastingBlock {

    public CastingTableBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_14PX.get(Direction.UP);
    }

    @Override
    public Class<CastingBlockEntity> getBlockEntityClass() {
        return CastingBlockEntity.class;
    }

    public BlockEntityType<? extends CastingTableBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.CASTING_TABLE.get();
    }
}