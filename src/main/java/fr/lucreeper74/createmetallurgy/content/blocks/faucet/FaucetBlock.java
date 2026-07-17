package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

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
    public boolean canSurvive(BlockState state, LevelReader readerLevel, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(direction.getOpposite());

        if (readerLevel instanceof Level level) {
            BlockEntity blockEntity = level.getBlockEntity(attachedPos);
            if (blockEntity == null)
                return false;

            IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, attachedPos, direction);
            if (capability != null && capability.getTanks() > 0)
                return true;

            capability = level.getCapability(Capabilities.FluidHandler.BLOCK, attachedPos, null);
            return (capability != null && capability.getTanks() > 0);
        }

        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        withBlockEntityDo(level, pos, be -> {
            if (be.canOpenFaucet())
                toggleFaucet(state, level, pos);
        });
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN))
                playSound(level, pos, flag);
            level.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
        } else
            level.setBlock(pos, state, 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, POWERED);
        super.createBlockStateDefinition(builder);
    }

    public static void toggleFaucet(BlockState state, Level level, BlockPos pos) {
        boolean newState = !state.getValue(OPEN);
        level.setBlockAndUpdate(pos, state.setValue(OPEN, newState));
        playSound(level, pos, newState);
    }

    private static void playSound(Level level, BlockPos pos, boolean isOpen) {
        level.playSound(null, pos, isOpen ? BlockSetType.IRON.trapdoorOpen() : BlockSetType.IRON.trapdoorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(null, isOpen ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
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