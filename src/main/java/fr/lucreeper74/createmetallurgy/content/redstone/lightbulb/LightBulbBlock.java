package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBulbBlock extends WrenchableDirectionalBlock implements IBE<LightBulbBlockEntity>, SimpleWaterloggedBlock {

    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final DyeColor color;

    public LightBulbBlock(Properties pProperties, DyeColor color) {
        super(pProperties);
        this.color = color;
        registerDefaultState(super.defaultBlockState()
                .setValue(LEVEL, 0)
                .setValue(WATERLOGGED, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        FluidState fluidstate = level.getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;

        int signal = level.getBestNeighborSignal(pContext.getClickedPos());

        return this.defaultBlockState()
                .setValue(FACING, pContext.getClickedFace())
                .setValue(WATERLOGGED, Boolean.valueOf(flag))
                .setValue(LEVEL, signal);
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> Shapes.or(Block.box(5, 0, 5, 11, 12, 11));
            case DOWN -> Shapes.or(Block.box(5, 4, 5, 11, 16, 11));
            case NORTH -> Shapes.or(Block.box(5, 5, 4, 11, 11, 16));
            case SOUTH -> Shapes.or(Block.box(5, 5, 0, 11, 11, 12));
            case EAST -> Shapes.or(Block.box(0, 5, 5, 12, 11, 11));
            case WEST -> Shapes.or(Block.box(4, 5, 5, 16, 11, 11));
        };
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide())
            return;

        if (state.getValue(LEVEL) == worldIn.getBestNeighborSignal(pos))
            return;
        transmit(worldIn, pos);
    }

    public void transmit(Level worldIn, BlockPos pos) {
        if (worldIn.isClientSide)
            return;

        int signal = worldIn.getBestNeighborSignal(pos);

        withBlockEntityDo(worldIn, pos,
                be -> be.transmit(signal));
    }


    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public Class<LightBulbBlockEntity> getBlockEntityClass() {
        return LightBulbBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LightBulbBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.LIGHT_BULB.get();
    }
}
