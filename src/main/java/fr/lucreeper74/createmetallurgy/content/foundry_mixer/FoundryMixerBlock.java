package fr.lucreeper74.createmetallurgy.content.foundry_mixer;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FoundryMixerBlock extends MechanicalMixerBlock implements ICogWheel {
    public FoundryMixerBlock(Properties properties) {
        super(properties);
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !CMBlocks.FOUNDRY_BASIN_BLOCK.has(worldIn.getBlockState(pos.below()));
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext
                && ((EntityCollisionContext) context).getEntity() instanceof Player)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    public BlockEntityType<? extends FoundryMixerBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.FOUNDRY_MIXER.get();
    }
}
