package fr.lucreeper74.createmetallurgy.content.blocks.labelling_station;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;

public class LabellingStationBlock extends WrenchableDirectionalBlock implements IBE<LabellingStationBlockEntity>, IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    public LabellingStationBlock(Properties properties) {
        super(properties);
        BlockState defaultBlockState = defaultBlockState();
        if (defaultBlockState.hasProperty(LINKED))
            defaultBlockState = defaultBlockState.setValue(LINKED, false);
        registerDefaultState(defaultBlockState.setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Capability<IItemHandler> itemCap = ForgeCapabilities.ITEM_HANDLER;
        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockEntity be = context.getLevel()
                    .getBlockEntity(context.getClickedPos()
                            .relative(face));
            if (be instanceof PackagerBlockEntity)
                continue;
            if (be != null && (be.getCapability(itemCap)
                    .isPresent())) {
                preferredFacing = face.getOpposite();
                break;
            }
        }

        Player player = context.getPlayer();
        if (preferredFacing == null) {
            Direction facing = context.getNearestLookingDirection();
            preferredFacing = player != null && player
                    .isShiftKeyDown() ? facing : facing.getOpposite();
        }

        if (player != null && !(player instanceof FakePlayer)) {
            if (AllBlocks.PORTABLE_STORAGE_INTERFACE.has(context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(preferredFacing.getOpposite())))) {
                CreateLang.translate("packager.no_portable_storage")
                        .sendStatus(player);
                return null;
            }
        }

        return super.getStateForPlacement(context).setValue(POWERED, context.getLevel()
                        .hasNeighborSignal(context.getClickedPos()))
                .setValue(FACING, preferredFacing);
    }

    /*@Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (player == null)
            return InteractionResult.PASS;

        ItemStack itemInHand = player.getItemInHand(handIn);
        if (AllItems.WRENCH.isIn(itemInHand))
            return InteractionResult.PASS;
        if (AllBlocks.FACTORY_GAUGE.isIn(itemInHand))
            return InteractionResult.PASS;
        if (AllBlocks.STOCK_LINK.isIn(itemInHand) && !(state.hasProperty(LINKED) && state.getValue(LINKED)))
            return InteractionResult.PASS;
        if (AllBlocks.PACKAGE_FROGPORT.isIn(itemInHand))
            return InteractionResult.PASS;

        if (onBlockEntityUse(worldIn, pos, be -> {
            if (be.containedLadle.isEmpty()) {
                if (be.animationTicks > 0)
                    return InteractionResult.SUCCESS;
                if (LadleItem.isLadle(itemInHand)) {
                    if (worldIn.isClientSide())
                        return InteractionResult.SUCCESS;
                    AllSoundEvents.DEPOT_PLOP.playOnServer(worldIn, pos);
                    if (itemInHand.isEmpty())
                        player.setItemInHand(handIn, ItemStack.EMPTY);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.SUCCESS;
            }
            if (be.animationTicks > 0)
                return InteractionResult.SUCCESS;
            if (!worldIn.isClientSide()) {
                player.getInventory()
                        .placeItemBackInInventory(be.containedLadle.copy());
                AllSoundEvents.playItemPickup(player);
                be.containedLadle = ItemStack.EMPTY;
                be.notifyUpdate();
            }
            return InteractionResult.SUCCESS;
        }))
            return InteractionResult.SUCCESS;

        return InteractionResult.SUCCESS;
    }*/

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED, LINKED));
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;
        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered == worldIn.hasNeighborSignal(pos))
            return;
        worldIn.setBlock(pos, state.cycle(POWERED), 2);

        if (state.getValue(POWERED)) {
            onBlockEntityUse(worldIn, pos, be -> {
                be.activate();
                return InteractionResult.SUCCESS;
            });
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }


    @Override
    public Class<LabellingStationBlockEntity> getBlockEntityClass() {
        return LabellingStationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LabellingStationBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.LABELLING_STATION.get();
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return getBlockEntityOptional(pLevel, pPos).map(pbe -> {
                    boolean empty = pbe.ladleInv.getStackInSlot(0)
                            .isEmpty();
                    if (pbe.animationTicks != 0)
                        empty = false;
                    return empty ? 0 : 15;
                })
                .orElse(0);
    }
}