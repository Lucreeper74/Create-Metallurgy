package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FaucetBlock extends WrenchableDirectionalBlock implements IBE<FaucetBlockEntity> {

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public FaucetBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState().setValue(OPEN, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction face = pContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, face.equals(Direction.UP) ? Direction.NORTH : face)
                .setValue(OPEN, false)
                .setValue(POWERED, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isHorizontal())
            return CMShapes.FAUCET.get(facing);
        else
            return CMShapes.FAUCET_DOWN;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean currentState = state.getValue(OPEN);
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        level.setBlock(pos, state.cycle(OPEN), 3);
        level.levelEvent(player, currentState ? 1036 : 1037, pos, 0);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide())
            withBlockEntityDo(level, pos, be -> be.neighborChanged(neighborPos));

        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN))
                level.levelEvent(null, flag ? 1036 : 1037, pos, 0);
            level.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
        } else {
            level.setBlock(pos, state, 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN)
                .add(POWERED);
        super.createBlockStateDefinition(builder);
    }


    @Override
    public Class<FaucetBlockEntity> getBlockEntityClass() {
        return FaucetBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FaucetBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.FAUCET.get();
    }
}
