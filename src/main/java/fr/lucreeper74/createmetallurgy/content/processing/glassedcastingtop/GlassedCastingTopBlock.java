package fr.lucreeper74.createmetallurgy.content.processing.glassedcastingtop;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import fr.lucreeper74.createmetallurgy.content.kinetics.foundrymixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GlassedCastingTopBlock extends Block implements IBE<GlassedCastingTopBlockEntity>, IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty UNDER_FOUNDRY_MIXER = BooleanProperty.create("under_foundry_mixer");
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public GlassedCastingTopBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState().setValue(UNDER_FOUNDRY_MIXER, false));
        registerDefaultState(super.defaultBlockState().setValue(OPEN, false));
        registerDefaultState(super.defaultBlockState().setValue(POWERED, false));
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.or(Block.box(1,0,1,15,14,15),
                Block.box(3,13,3,13,15,13));

    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(state, level, pos, p_60569_, p_60570_);

        if(level.getBlockEntity(pos.above()) instanceof FoundryMixerBlockEntity)
            level.setBlock(pos, state.setValue(UNDER_FOUNDRY_MIXER, true), 1) ;
        else
            level.setBlock(pos, state.setValue(UNDER_FOUNDRY_MIXER, false), 1);

    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_57551_, boolean p_57552_) {
        boolean flag = level.hasNeighborSignal(pos);
        if(level.getBlockEntity(pos.above()) instanceof FoundryMixerBlockEntity)
            state = state.setValue(UNDER_FOUNDRY_MIXER, true);
        else
            state = state.setValue(UNDER_FOUNDRY_MIXER, false);
        if (flag != state.getValue(POWERED)) {
            if(flag != state.getValue(OPEN))
                level.levelEvent(null, flag ? 1037:1036, pos, 0);
            level.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
        } else {
            level.setBlock(pos, state, 2);
        }
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        boolean currentState = state.getValue(OPEN);
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            level.setBlock(pos, state.setValue(OPEN, !currentState), 3);
            level.levelEvent(null, currentState ? 1037:1036, pos, 0);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNDER_FOUNDRY_MIXER);
        builder.add(FACING);
        builder.add(OPEN);
        builder.add(POWERED);
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
    public Class<GlassedCastingTopBlockEntity> getBlockEntityClass() {
        return GlassedCastingTopBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GlassedCastingTopBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.GLASSED_FOUNDRY_TOP.get();
    }
}