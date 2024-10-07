package fr.lucreeper74.createmetallurgy.content.fluids;

import com.simibubi.create.AllFluids;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class MoltenFluidType extends AllFluids.TintedFluidType {

    public MoltenFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return 0xFFFFFFFF;
    }

    @Override
    protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return 0xFFFFFFF;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.6d));
        entity.hurt(CMDamageTypes.moltenFluid(entity.level()), 4.0F);
        entity.setSecondsOnFire(15);

        return false;
    }

    public boolean canExtinguish(Entity entity) {
        return false;
    }
}
