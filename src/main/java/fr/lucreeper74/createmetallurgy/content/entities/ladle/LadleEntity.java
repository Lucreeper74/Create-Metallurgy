package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import java.util.List;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;

import fr.lucreeper74.createmetallurgy.mixins.accessors.PackageEntityAccessor;
import fr.lucreeper74.createmetallurgy.registries.CMEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class LadleEntity extends PackageEntity implements IHaveGoggleInformation {

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
        PackageEntityAccessor accessor = (PackageEntityAccessor) ladleEntity;
        accessor.setOriginalEntity(originalEntity);

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
        // Survive to water unlike packages
        // Todo: make the molten metal disappear when under water
    }

    @Override
    protected void dropAllDeathLoot(DamageSource pDamageSource) {
        super.dropAllDeathLoot(pDamageSource); // Todo: check if that a problem (or make spill fluid if dead)
    }

    public static LadleEntity spawn(SpawnEntity spawnEntity, Level world) {
        LadleEntity ladleEntity =
                new LadleEntity(world, spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ());
        ladleEntity.setDeltaMovement(spawnEntity.getVelX(), spawnEntity.getVelY(), spawnEntity.getVelZ());
        ladleEntity.clientPosition = ladleEntity.position();
        return ladleEntity;
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<LadleEntity> boxBuilder = (EntityType.Builder<LadleEntity>) builder;
        return boxBuilder.setCustomClientFactory(LadleEntity::spawn)
                .sized(1, 1);
    }

    public static void spawnParticles(Level level, Vec3 worldPos) {
        RandomSource r = level.getRandom();
        Vec3 v = worldPos.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                .multiply(1f, 0, 1f));

        if (r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.SMOKE, v.x, v.y, v.z, 0, 0, 0);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, FluidUtil.getFluidHandler(box).cast());
        // Todo: Fix this for fluid tooltip on entity
    }
}