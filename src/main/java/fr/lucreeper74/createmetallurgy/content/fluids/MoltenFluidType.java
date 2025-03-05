package fr.lucreeper74.createmetallurgy.content.fluids;

import com.simibubi.create.AllFluids;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class MoltenFluidType extends AllFluids.TintedFluidType {
    public static final DamageSource MOLTEN_FLUID_DAMAGE = new DamageSource(CreateMetallurgy.MOD_ID + ".molten_fluid").setIsFire();
    public static final int MOLTEN_FLUID_BURNING_TIME = 15;

    public MoltenFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return 0xFFFFFFFF;
    }

    @Override
    protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return 0xFFFFFFFF;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(.3F, .8F, .3F));
        if (!entity.fireImmune()) {
            entity.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
            if (entity.hurt(MOLTEN_FLUID_DAMAGE, 4.0F))
                entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
        }
        return false;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        if (entity.fireImmune()) {
            Vec3 vec3 = entity.getDeltaMovement();
            entity.setDeltaMovement(vec3.x * (double) .95F, vec3.y + (double) (vec3.y < (double) .06F ? 5.0E-4F : .0F), vec3.z * (double) .95F);
        } else {
            entity.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
            if (entity.hurt(MOLTEN_FLUID_DAMAGE, 4.0F))
                entity.playSound(SoundEvents.GENERIC_BURN, .4F, 3F);
        }
    }

    @Override
    public boolean supportsBoating(Boat boat) {
        boat.setSecondsOnFire(MOLTEN_FLUID_BURNING_TIME);
        return super.supportsBoating(boat);
    }

    public boolean canExtinguish(Entity entity) {
        return false;
    }
}
