package fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class FoundryLidBlock extends HorizontalDirectionalBlock implements IBE<FoundryLidBlockEntity>, IWrenchable {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WINDOW = BooleanProperty.create("window");

    public static final MapCodec<FoundryLidBlock> CODEC = simpleCodec(FoundryLidBlock::new);

    public FoundryLidBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState()
                .setValue(OPEN, false)
                .setValue(POWERED, false)
                .setValue(WINDOW, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CMShapes.FOUNDRY_LID;

    }

    protected void playSound(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, boolean pIsOpened) {
        pLevel.playSound(pPlayer, pPos, pIsOpened ? BlockSetType.IRON.trapdoorOpen() : BlockSetType.IRON.trapdoorClose(), SoundSource.BLOCKS, 1.0F, pLevel.getRandom().nextFloat() * 0.1F + 0.9F);
        pLevel.gameEvent(pPlayer, pIsOpened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_57551_, boolean p_57552_) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN))
                playSound(null, level, pos, flag);
            level.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
        } else {
            level.setBlock(pos, state, 2);
        }
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        level.setBlockAndUpdate(clickedPos, state.cycle(WINDOW));
        level.playSound(null, clickedPos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.PLAYERS, 1f,
                .2f + RandomSource.create().nextFloat());
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean currentState = state.getValue(OPEN);
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (player.isCrouching()) {
            level.setBlock(pos, state.setValue(OPEN, !currentState), 3);
            playSound(null, level, pos, currentState);
            return ItemInteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && state.getValue(OPEN)) {
            if (level.getBlockEntity(pos.below()) instanceof FoundryBasinBlockEntity basinBE) {
                ItemStack insertItem = ItemHandlerHelper.insertItem(basinBE.getInputInventory(), heldItem, false);
                player.setItemInHand(hand, heldItem.split(insertItem.getCount()));
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM,
                        SoundSource.PLAYERS, 1f, 1f + Create.RANDOM.nextFloat());
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, WINDOW);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (pContext.getPlayer().isShiftKeyDown()) {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        } else {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
        }
    }

    @Override
    public Class<FoundryLidBlockEntity> getBlockEntityClass() {
        return FoundryLidBlockEntity.class;
    }

    public BlockEntityType<? extends FoundryLidBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.FOUNDRY_LID.get();
    }
}
