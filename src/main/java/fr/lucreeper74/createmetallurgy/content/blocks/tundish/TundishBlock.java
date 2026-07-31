package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class TundishBlock extends Block implements IWrenchable, IBE<TundishBlockEntity> {

    public static final BooleanProperty ALONG_Z_AXIS = BooleanProperty.create("along_z_axis");
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty REAR = BooleanProperty.create("rear");

    public TundishBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FRONT, false).setValue(REAR, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ALONG_Z_AXIS, FRONT, REAR);
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
        return shape.get(state.getValue(ALONG_Z_AXIS) ? Direction.Axis.Z : Direction.Axis.X);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean alongZAxis = state.getValue(ALONG_Z_AXIS);
        Direction.Axis axis = alongZAxis ? Direction.Axis.Z : Direction.Axis.X;
        if (neighborState.is(CMBlocks.TUNDISH_BLOCK)) {
            BlockPos relativePos = pos.subtract(neighborPos);
            if ((alongZAxis && relativePos.getZ() == 0) || (!alongZAxis && relativePos.getX() == 0))
                return state;
        }


        Direction frontDir = getFacingDirection(state);
        BlockState frontState = level.getBlockState(pos.relative(frontDir));
        boolean frontValid = frontState.is(CMBlocks.TUNDISH_BLOCK) && frontState.getValue(ALONG_Z_AXIS).equals(alongZAxis);
        int frontLength = getLength(level, state, pos, frontDir);

        Direction rearDir = getFacingDirection(state).getOpposite();
        BlockState rearState = level.getBlockState(pos.relative(rearDir));
        boolean rearValid = rearState.is(CMBlocks.TUNDISH_BLOCK) && rearState.getValue(ALONG_Z_AXIS).equals(alongZAxis);
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction contextDir = context.getHorizontalDirection();
        Direction.Axis axis = contextDir.getAxis();

        boolean alongZAxis = axis.equals(Direction.Axis.Z);
        boolean towardFront = contextDir.equals(Direction.get(Direction.AxisDirection.POSITIVE, axis));

        BlockState dirState = level.getBlockState(pos.relative(contextDir));
        boolean isValid = dirState.is(CMBlocks.TUNDISH_BLOCK) && dirState.getValue(ALONG_Z_AXIS).equals(alongZAxis);

        return this.defaultBlockState().setValue(ALONG_Z_AXIS, alongZAxis)
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
        Direction.Axis axis = state.getValue(TundishBlock.ALONG_Z_AXIS) ? Direction.Axis.Z : Direction.Axis.X;
        return Direction.get(Direction.AxisDirection.POSITIVE, axis);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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
}
