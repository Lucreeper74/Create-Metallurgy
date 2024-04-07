package fr.lucreeper74.createmetallurgy.content.processing.foundrylid;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.processing.foundrybasin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

import static fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidBlock.OPEN;

public class FoundryLidBlockEntity extends BasinOperatingBlockEntity {

    public int processingTime;
    public boolean running;

    public FoundryLidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("MeltingTime", processingTime);
        compound.putBoolean("Running", running);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        processingTime = compound.getInt("MeltingTime");
        running = compound.getBoolean("Running");
    }

    @Override
    protected void onBasinRemoved() {
        if (!running) return;
        processingTime = 0;
        currentRecipe = null;
        running = false;
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == CMRecipeTypes.MELTING.getType();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide && (currentRecipe == null || processingTime == -1)) {
            running = false;
            processingTime = -1;
            basinChecker.scheduleUpdate();
        }

        if (running && level != null) {
            if (!level.isClientSide && processingTime <= 0) {
                processingTime = -1;
                applyBasinRecipe();
                sendData();
            }
            if(!level.isClientSide && processingTime % 40 == 0){
                level.playSound(null, worldPosition, SoundEvents.LAVA_AMBIENT,
                        SoundSource.BLOCKS, .5f, .75f);
            }

            if(level.isClientSide && processingTime % 2 == 0)
                spawnParticles();

            if (processingTime > 0) --processingTime;
        }
    }
    protected void spawnParticles() {
        RandomSource r = level.getRandom();
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));

        if (r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y + .45, v.z, 0, 0, 0);
    }

    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity basinBE = level.getBlockEntity(worldPosition.below());
        if (!(basinBE instanceof FoundryBasinBlockEntity))
            return Optional.empty();
        if(getBlockState().getValue(OPEN))
            return Optional.empty();
        return Optional.of((FoundryBasinBlockEntity) basinBE);
    }

    @Override
    protected boolean updateBasin() {
        if (running) return true;
        if (level == null || level.isClientSide) return true;
        if (getBasin().filter(BasinBlockEntity::canContinueProcessing).isEmpty()) return true;

        List<Recipe<?>> recipes = getMatchingRecipes();
        if (recipes.isEmpty()) return true;
        currentRecipe = recipes.get(0);
        startProcessingBasin();
        sendData();
        return true;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    public void startProcessingBasin() {
        if (running && processingTime > 0) return;
        super.startProcessingBasin();
        running = true;
        processingTime = currentRecipe instanceof ProcessingRecipe<?> processed ? processed.getProcessingDuration() : 20;
    }

    private static final Object MeltingRecipesKey = new Object();
    @Override
    protected Object getRecipeCacheKey() {
        return MeltingRecipesKey;
    }
}