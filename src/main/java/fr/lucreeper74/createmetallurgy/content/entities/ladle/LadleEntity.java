package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import fr.lucreeper74.createmetallurgy.registries.CMEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class LadleEntity extends PackageEntity {

    public LadleEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public LadleEntity(Level worldIn, double x, double y, double z) {
        this(CMEntityTypes.LADLE.get(), worldIn);
        this.setPos(x, y, z);
        this.refreshDimensions();
    }

    public static LadleEntity fromDroppedItem(Level world, Entity originalEntity, ItemStack itemstack) {
        LadleEntity ladleEntity = CMEntityTypes.LADLE.get()
                .create(world);

        Vec3 position = originalEntity.position();
        ladleEntity.setPos(position);
        ladleEntity.setBox(itemstack);
        ladleEntity.setDeltaMovement(originalEntity.getDeltaMovement()
                .scale(1.5f));

        if (!world.isClientSide)
            if (ChuteBlock.isChute(world.getBlockState(BlockPos.containing(position.x, position.y + .5f, position.z))))
                ladleEntity.setYRot((float) ((int) ladleEntity.getYRot()) / 90 * 90);

        return ladleEntity;
    }

    public static LadleEntity fromItemStack(Level world, Vec3 position, ItemStack itemstack) {
        LadleEntity LadleEntity = CMEntityTypes.LADLE.get()
                .create(world);
        LadleEntity.setPos(position);
        LadleEntity.setBox(itemstack);
        return LadleEntity;
    }

    @Override
    public void tick() {
        super.tick();
        // Todo: add lerped fluid level chasing

        if (level().isClientSide()) {
            if (!(box.getItem() instanceof LadleItem))
                return;

            FluidStack containedFluid = FluidUtil.getFluidContained(box).orElse(FluidStack.EMPTY);
            if (!CMFluids.isMoltenMaterial(containedFluid.getFluid()))
                return;

            spawnParticles(level(), getEyePosition());
        }


    }

    @Override
    protected void onInsideBlock(BlockState state) {
        if (!isAlive())
            return;
        if (state.getBlock() == Blocks.WATER || (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))) {
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(box).orElse(null);
            if (fluidHandler != null) {
                for (boolean simulate : Iterate.trueAndFalse) {
                    IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;

                    FluidStack drained = fluidHandler.drain(LadleItem.getLadleCapacity(), action);
                    Fluid fluid = drained.getFluid();
                    if (!CMFluids.isHotFluid(fluid))
                        return;

                    if (!simulate) {
                        float pitch = 1.8f - RandomSource.create().nextFloat() * .4f;
                        level().playSound(null, blockPosition(),
                                SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, .5f, pitch);
                    }
                }
            }
        }
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource pDamageSource) {
        super.dropAllDeathLoot(level, pDamageSource);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<LadleEntity> boxBuilder = (EntityType.Builder<LadleEntity>) builder;
        return boxBuilder.sized(1, 1); // .setCustomClientFactory(LadleEntity::spawn)
    }

    public static void spawnParticles(Level level, Vec3 worldPos) {
        RandomSource r = level.getRandom();
        Vec3 v = worldPos.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                .multiply(1f, 0, 1f));

        if (r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.SMOKE, v.x, v.y, v.z, 0, 0, 0);
    }
}