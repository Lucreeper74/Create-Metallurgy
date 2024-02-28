package fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.registries.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MechanicalBeltGrinderBlock extends HorizontalKineticBlock implements IBE<MechanicalBeltGrinderBlockEntity> {
    public MechanicalBeltGrinderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_12PX.get(Direction.UP);
    }

    @Override
    public Class<MechanicalBeltGrinderBlockEntity> getBlockEntityClass() {
        return MechanicalBeltGrinderBlockEntity.class;
    }

    public BlockEntityType<? extends MechanicalBeltGrinderBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.MECHANICAL_BELT_GRINDER.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }
}
