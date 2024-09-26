package fr.lucreeper74.createmetallurgy.content.fluids;

import com.simibubi.create.AllFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class MoltenFluidType extends AllFluids.TintedFluidType {
    private static final DamageSource MOLTEN_FLUID_DAMAGE = new DamageSource("createmetallurgy.molten_fluid").setScalesWithDifficulty()
            .setIsFire();

    public MoltenFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return 0x00ffffff;
    }

    @Override
    protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return 0x00ffffff;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.6d));
        entity.hurt(MOLTEN_FLUID_DAMAGE, 4.0F);
        entity.setSecondsOnFire(15);

        return false;
    }

    public boolean canExtinguish(Entity entity) {
        return false;
    }
}
