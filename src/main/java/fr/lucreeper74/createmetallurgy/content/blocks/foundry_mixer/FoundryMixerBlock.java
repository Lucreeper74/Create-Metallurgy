package fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FoundryMixerBlock extends MechanicalMixerBlock implements ICogWheel {
    public FoundryMixerBlock(Properties properties) {
        super(properties);
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !CMBlocks.FOUNDRY_BASIN_BLOCK.has(worldIn.getBlockState(pos.below()));
    }

    public BlockEntityType<? extends FoundryMixerBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.FOUNDRY_MIXER.get();
    }
}
