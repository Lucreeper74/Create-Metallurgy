package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LightBulbBlock extends RodBlock implements IBE<LightBulbBlockEntity>, IWrenchable, SimpleWaterloggedBlock {

    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public LightBulbBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(LEVEL, 0)
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;

        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace())
                .setValue(WATERLOGGED, Boolean.valueOf(flag))
                .setValue(LEVEL, pContext.getLevel().getBestNeighborSignal(pContext.getClickedPos()));
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL)
                .add(FACING)
                .add(WATERLOGGED);
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

    private void updateLevelState(Level level, BlockPos pos) {
        if (level.isClientSide())
            return;

        level.setBlock(pos, level.getBlockState(pos).setValue(LEVEL, level.getBestNeighborSignal(pos)), 2);
        getBlockEntity(level, pos).buildQueue();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(level.isClientSide())
            return;

        getBlockEntity(level, pos).searchNearLight(8);
        updateLevelState(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(level.isClientSide())
            return;

        if (state.getValue(LEVEL) == level.getBestNeighborSignal(pos))
            return;
        updateLevelState(level, pos);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        //This called when the block is destroyed & when state change
        //If newState == Minecraft:Air -> The block is destroyed
        if(newState != Blocks.AIR.defaultBlockState())
            return;
        if (level == null || level.isClientSide)
            return;

        getBlockEntity(level, pos).getNetwork().removeNode(pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
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
