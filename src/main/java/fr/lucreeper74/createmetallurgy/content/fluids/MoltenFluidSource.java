package fr.lucreeper74.createmetallurgy.content.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class MoltenFluidSource extends BaseFlowingFluid.Source {
    public MoltenFluidSource(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom) {
        super.animateTick(pLevel, pPos, pState, pRandom);

        BlockPos blockpos = pPos.above();
        if (pLevel.getBlockState(blockpos).isAir() && !pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
            if (pRandom.nextInt(100) == 0) {
                double d0 = (double) pPos.getX() + pRandom.nextDouble();
                double d1 = (double) pPos.getY() + (double) 1.0F;
                double d2 = (double) pPos.getZ() + pRandom.nextDouble();
                pLevel.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0F, 0.0F, 0.0F);
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + pRandom.nextFloat() * 0.2F, 0.9F + pRandom.nextFloat() * 0.15F, false);
            }

            if (pRandom.nextInt(200) == 0) {
                pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + pRandom.nextFloat() * 0.2F, 0.9F + pRandom.nextFloat() * 0.15F, false);
            }
        }
    }

    @Override
    public void randomTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom) {
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            int i = pRandom.nextInt(3);
            if (i > 0) {
                BlockPos blockpos = pPos;

                for (int j = 0; j < i; ++j) {
                    blockpos = blockpos.offset(pRandom.nextInt(3) - 1, 1, pRandom.nextInt(3) - 1);
                    if (!pLevel.isLoaded(blockpos)) {
                        return;
                    }

                    BlockState blockstate = pLevel.getBlockState(blockpos);
                    if (blockstate.isAir()) {
                        if (this.hasFlammableNeighbours(pLevel, blockpos)) {
                            pLevel.setBlockAndUpdate(blockpos, EventHooks.fireFluidPlaceBlockEvent(pLevel, blockpos, pPos, Blocks.FIRE.defaultBlockState()));
                            return;
                        }
                    } else if (blockstate.blocksMotion()) {
                        return;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPos blockpos1 = pPos.offset(pRandom.nextInt(3) - 1, 0, pRandom.nextInt(3) - 1);
                    if (!pLevel.isLoaded(blockpos1)) {
                        return;
                    }

                    if (pLevel.isEmptyBlock(blockpos1.above()) && this.isFlammable(pLevel, blockpos1, Direction.UP)) {
                        pLevel.setBlockAndUpdate(blockpos1.above(), EventHooks.fireFluidPlaceBlockEvent(pLevel, blockpos1.above(), pPos, Blocks.FIRE.defaultBlockState()));
                    }
                }
            }

        }
    }

    private boolean hasFlammableNeighbours(LevelReader pLevel, BlockPos pPos) {
        for (Direction direction : Direction.values()) {
            if (this.isFlammable(pLevel, pPos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    private boolean isFlammable(LevelReader level, BlockPos pos, Direction face) {
        return (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight() || level.hasChunkAt(pos)) && level.getBlockState(pos).isFlammable(level, pos, face);
    }

    protected boolean isRandomlyTicking() {
        return true;
    }
}