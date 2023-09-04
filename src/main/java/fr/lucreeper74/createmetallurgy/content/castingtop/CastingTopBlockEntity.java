package fr.lucreeper74.createmetallurgy.content.castingtop;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.content.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.castingbasin.CastingBasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

import static fr.lucreeper74.createmetallurgy.content.castingtop.CastingTopBlock.OPEN;

public class CastingTopBlockEntity extends CastingBasinOperatingBlockEntity {
    public int processingTime;
    public boolean running;
    public CastingTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("MeltingTime", this.processingTime);
        compound.putBoolean("Running", this.running);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.processingTime = compound.getInt("MeltingTime");
        this.running = compound.getBoolean("Running");
    }

    @Override
    protected void onBasinRemoved() {
        if (!this.running) return;
        this.processingTime = 0;
        this.currentRecipe = null;
        this.running = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide && (this.currentRecipe == null || this.processingTime == -1)) {
            this.running = false;
            this.processingTime = -1;
            this.basinChecker.scheduleUpdate();
        }

        if (this.running && this.level != null) {
            if (!this.level.isClientSide && this.processingTime <= 0) {
                this.processingTime = -1;
                this.applyBasinRecipe();
                this.sendData();
            }
            if(!this.level.isClientSide && processingTime % 40 == 0){
                level.playSound(null, worldPosition, SoundEvents.LAVA_AMBIENT,
                        SoundSource.BLOCKS, .5f, .75f);
            }

            if(this.level.isClientSide && processingTime % 2 == 0) {
                animateTick(this.level, worldPosition);
            }

            if (this.processingTime > 0) --this.processingTime;
        }
    }

    public void animateTick(Level pLevel, BlockPos pPos) {
        Vec3 center = VecHelper.getCenterOf(pPos);
        pLevel.addParticle(ParticleTypes.SMOKE, center.x, center.y + .45, center.z, 0D, 0.02D, 0D);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        boolean slow = Math.abs(getSpeed()) < 65;
        if (slow && AnimationTickHolder.getTicks() % 2 == 0)
            return;
        if (processingTime == 20)
            AllSoundEvents.MIXING.playAt(level, worldPosition, .75f, 1, true);
    }
    @Override
    protected boolean updateBasin() {
        if (this.running) return true;
        if (this.level == null || this.level.isClientSide) return true;
        if (this.getBasin().filter(BasinBlockEntity::canContinueProcessing).isEmpty()) return true;

        List<Recipe<?>> recipes = this.getMatchingRecipes();
        if (recipes.isEmpty()) return true;
        this.currentRecipe = recipes.get(0);
        this.startProcessingBasin();
        this.sendData();
        return true;
    }

    @Override
    public void startProcessingBasin() {
        if (this.running && this.processingTime > 0) return;
        super.startProcessingBasin();
        this.running = true;
        this.processingTime = this.currentRecipe instanceof ProcessingRecipe<?> processed ? processed.getProcessingDuration() : 20;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }


    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity basinBE = level.getBlockEntity(worldPosition.below(1));
        if (!(basinBE instanceof CastingBasinBlockEntity))
            return Optional.empty();
        if(getBlockState().getValue(OPEN))
            return Optional.empty();
        return Optional.of((CastingBasinBlockEntity) basinBE);
    }

    private static final Object MeltingRecipesKey = new Object();
    @Override
    protected Object getRecipeCacheKey() {
        return MeltingRecipesKey;
    }
}
