package fr.lucreeper74.createmetallurgy.content.foundry_mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.foundry_lid.FoundryLidBlock;
import fr.lucreeper74.createmetallurgy.content.glassed_foundry_lid.GlassedFoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FoundryMixerBlockEntity extends MechanicalMixerBlockEntity {

    public FoundryMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == CMRecipeTypes.ALLOYING.getType();
    }

    @Override
    public void tick() {
        super.tick();

        if(level != null && !running) {
            if (!level.isClientSide) {
                basinChecker.scheduleUpdate();
            }
        }
    }

    @Override
    protected void spillParticle(ParticleOptions data) {
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.25f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y)
                .add(0, .25f, 0);
        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y - 1.65f, center.z, target.x, target.y, target.z);
    }

    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity basinBE = level.getBlockEntity(worldPosition.below(2));
        BlockEntity topBE = level.getBlockEntity(worldPosition.below());
        if (!(basinBE instanceof FoundryBasinBlockEntity && topBE instanceof GlassedFoundryLidBlockEntity))
            return Optional.empty();
        if(topBE.getBlockState().getValue(FoundryLidBlock.OPEN))
                return Optional.empty();
        return Optional.of((FoundryBasinBlockEntity) basinBE);
    }

    private static final Object AlloyingRecipesKey = new Object();
    @Override
    protected Object getRecipeCacheKey() {
        return AlloyingRecipesKey;
    }
}