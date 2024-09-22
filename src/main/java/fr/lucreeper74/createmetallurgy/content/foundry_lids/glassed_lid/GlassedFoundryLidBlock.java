package fr.lucreeper74.createmetallurgy.content.foundry_lids.glassed_lid;

import fr.lucreeper74.createmetallurgy.content.foundry_lids.LidBlock;
import fr.lucreeper74.createmetallurgy.content.foundry_mixer.FoundryMixerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class GlassedFoundryLidBlock extends LidBlock {
    public static final BooleanProperty UNDER_FOUNDRY_MIXER = BooleanProperty.create("under_foundry_mixer");

    public GlassedFoundryLidBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState().setValue(UNDER_FOUNDRY_MIXER, false));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        if (level.getBlockEntity(pos.above()) instanceof FoundryMixerBlockEntity)
            state = state.setValue(UNDER_FOUNDRY_MIXER, true);
        else
            state = state.setValue(UNDER_FOUNDRY_MIXER, false);

        super.onPlace(state, level, pos, p_60569_, p_60570_);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_57551_, boolean p_57552_) {
        if (level.getBlockEntity(pos.above()) instanceof FoundryMixerBlockEntity)
            state = state.setValue(UNDER_FOUNDRY_MIXER, true);
        else
            state = state.setValue(UNDER_FOUNDRY_MIXER, false);
        super.neighborChanged(state, level, pos, block, p_57551_, p_57552_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNDER_FOUNDRY_MIXER);
        super.createBlockStateDefinition(builder);
    }
}