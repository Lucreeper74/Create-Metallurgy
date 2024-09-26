package fr.lucreeper74.createmetallurgy.content.foundry_mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.foundry_lids.glassed_lid.GlassedFoundryLidBlock;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
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

        if (level != null && !running) {
            if (!level.isClientSide) {
                basinChecker.scheduleUpdate();
            }
        }
    }

    @Override
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (recipe == null)
            return false;
        Optional<BasinBlockEntity> basin = getBasin();
        if (!basin.isPresent())
            return false;
        return FoundryBasinRecipe.match((FoundryBasinBlockEntity) basin.get(), recipe);
    }

    @Override
    protected void applyBasinRecipe() {
        if (currentRecipe == null)
            return;

        Optional<BasinBlockEntity> optionalBasin = getBasin();
        if (!optionalBasin.isPresent())
            return;
        FoundryBasinBlockEntity basin = (FoundryBasinBlockEntity) optionalBasin.get();
        boolean wasEmpty = basin.canContinueProcessing();
        if (!FoundryBasinRecipe.apply(basin, currentRecipe))
            return;
        getProcessedRecipeTrigger().ifPresent(this::award);
        basin.inputTank.sendDataImmediately();

        // Continue mixing
        if (wasEmpty && matchBasinRecipe(currentRecipe)) {
            continueWithPreviousRecipe();
            sendData();
        }

        basin.notifyChangeOfContents();
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
        Block top = level.getBlockState(worldPosition.below()).getBlock();
        if (!(basinBE instanceof FoundryBasinBlockEntity && top instanceof GlassedFoundryLidBlock))
            return Optional.empty();
        if (level.getBlockState(worldPosition.below()).getValue(GlassedFoundryLidBlock.OPEN))
            return Optional.empty();
        return Optional.of((FoundryBasinBlockEntity) basinBE);
    }

    private static final Object AlloyingRecipesKey = new Object();

    @Override
    protected Object getRecipeCacheKey() {
        return AlloyingRecipesKey;
    }
}