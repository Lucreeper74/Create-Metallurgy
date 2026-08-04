package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class TundishBlock extends Block implements IWrenchable, IBE<TundishBlockEntity> {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty REAR = BooleanProperty.create("rear");

    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public TundishBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FRONT, false)
                .setValue(REAR, false)
                .setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, FRONT, REAR);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        boolean front = state.getValue(FRONT);
        boolean rear = state.getValue(REAR);
        int shape_index = (front ? 1 : 0) + (rear ? 2 : 0);

        VoxelShaper shape = switch (shape_index) {
            case 1 -> CMShapes.TUNDISH_FRONT;
            case 2 -> CMShapes.TUNDISH_REAR;
            case 3 -> CMShapes.TUNDISH_MIDDLE;
            default -> CMShapes.TUNDISH_SINGLE;
        };
        return shape.get(state.getValue(AXIS));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = state.getValue(AXIS);
        boolean alongZAxis = axis.equals(Direction.Axis.Z);
        if (neighborState.is(CMBlocks.TUNDISH_BLOCK)) {
            BlockPos relativePos = pos.subtract(neighborPos);
            if ((alongZAxis && relativePos.getZ() == 0) || (!alongZAxis && relativePos.getX() == 0))
                return state;
        }


        Direction frontDir = getFacingDirection(state);
        BlockState frontState = level.getBlockState(pos.relative(frontDir));
        boolean frontValid = frontState.is(CMBlocks.TUNDISH_BLOCK) && frontState.getValue(AXIS).equals(axis);
        int frontLength = getLength(level, state, pos, frontDir);

        Direction rearDir = getFacingDirection(state).getOpposite();
        BlockState rearState = level.getBlockState(pos.relative(rearDir));
        boolean rearValid = rearState.is(CMBlocks.TUNDISH_BLOCK) && rearState.getValue(AXIS).equals(axis);
        int rearLength = getLength(level, state, pos, rearDir);

        int totalLength = (frontLength + rearLength) - 1; // Avoid counting twice the current block
        boolean totalLengthValid = totalLength <= CMConfig.server().tundishMaxLength.get() && totalLength > 0;

        boolean front = frontValid && frontLength <= CMConfig.server().tundishMaxLength.get() && frontLength > 0;
        boolean rear = rearValid && rearLength <= CMConfig.server().tundishMaxLength.get() && rearLength > 0;

        if (front && rear && !totalLengthValid) {
            level.scheduleTick(pos, state.getBlock(), 0);
            return state; // Both sides are valid but the total length will exceed
        }

        return state.setValue(FRONT, front).setValue(REAR, rear);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL); // Force neighbors update
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        withBlockEntityDo(level, pos, TundishBlockEntity::randomTick);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction ctxDir = context.getHorizontalDirection();
        Direction.Axis ctxAxis = ctxDir.getAxis();

        boolean towardFront = ctxDir.equals(Direction.get(Direction.AxisDirection.POSITIVE, ctxAxis));

        BlockState dirState = level.getBlockState(pos.relative(ctxDir));
        boolean isValid = dirState.is(CMBlocks.TUNDISH_BLOCK) && dirState.getValue(AXIS).equals(ctxAxis);

        return this.defaultBlockState().setValue(AXIS, ctxAxis)
                .setValue(FRONT, towardFront && isValid).setValue(REAR, !towardFront && isValid);
    }

    public static int getLength(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
        if (!state.is(CMBlocks.TUNDISH_BLOCK))
            return 0; // if not tundish or middle part

        /*
         *  return dist;   -> means all the previously checked blocks WITHOUT the current as length
         *  return dist+1; -> means all the previously checked blocks WITH the current as length
         */
        for (int dist = 1; dist <= CMConfig.server().tundishMaxLength.get(); dist++) {
            BlockState relativeState = level.getBlockState(pos.relative(direction, dist));
            if (!relativeState.is(CMBlocks.TUNDISH_BLOCK))
                return dist;

            if (relativeState.getValue(FRONT) && relativeState.getValue(REAR))
                continue; // ignore middle

            if (relativeState.getValue(direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? REAR : FRONT))
                return dist + 1; // if opposite side
            // if same side, continue
        }
        return 0;
    }

    public static Direction getFacingDirection(BlockState state) {
        return Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(TundishBlock.AXIS));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        return onBlockEntityUseItemOn(level, pos, be -> {
            if (!stack.isEmpty()) {
                if (stack.getItem().equals(Items.SPONGE)) {
                    IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
                    if (fluidHandler != null) {
                        FluidStack drained = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public Class<TundishBlockEntity> getBlockEntityClass() {
        return TundishBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TundishBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.TUNDISH.get();
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction.Axis> {

        public PlacementHelper() {
            super(
                    state -> state.is(CMBlocks.TUNDISH_BLOCK),
                    state -> state.getValue(AXIS),
                    AXIS
            );
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CMBlocks.TUNDISH_BLOCK::isIn;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> state.is(CMBlocks.TUNDISH_BLOCK);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state,
                                         BlockPos pos, BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful()) {
                offset.withTransform(s -> s.setValue(
                        AXIS,
                        state.getValue(AXIS)
                ));
            }
            return offset;
        }
    }
}
