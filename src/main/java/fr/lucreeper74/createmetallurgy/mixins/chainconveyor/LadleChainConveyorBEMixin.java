package fr.lucreeper74.createmetallurgy.mixins.chainconveyor;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleFluidVisual.MINY_LADLE;

@Mixin(ChainConveyorBlockEntity.class)
public class LadleChainConveyorBEMixin {

    @Redirect(
            method = "drop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            remap = false
    )
    private boolean dropRedirected(Level level, Entity originalEntity, ChainConveyorPackage box) {
        if (box.item.getItem() instanceof LadleItem)
            return level.addFreshEntity(LadleEntity.fromItemStack(level, box.worldPosition.subtract(0, .5f, 0), box.item));
        else if (box.item.getItem() instanceof PackageItem)
            return level.addFreshEntity(PackageEntity.fromItemStack(level, box.worldPosition.subtract(0, .5f, 0), box.item));

        return false;
    }

    @Inject(
            method = "tick",
            at = @At("RETURN"),
            remap = false
    )
    private void spawnParticles(CallbackInfo ci) {
        ChainConveyorBlockEntity be = (ChainConveyorBlockEntity) (Object) this; // This is ChainConveyorBlockEntity so safe here
        Level level = be.getLevel();

        if (be == null || level == null)
            return;

        if (!level.isClientSide)
            return;

        for (ChainConveyorPackage box : be.getLoopingPackages())
            CM$spawnLadleParticles(be, box, level);

        for (Map.Entry<BlockPos, List<ChainConveyorPackage>> entry : be.getTravellingPackages().entrySet())
            for (ChainConveyorPackage box : entry.getValue())
                CM$spawnLadleParticles(be, box, level);
    }

    @Unique
    private void CM$spawnLadleParticles(ChainConveyorBlockEntity be, ChainConveyorPackage box, Level level) {
        if (!(box.item.getItem() instanceof LadleItem))
            return;

        FluidStack containedFluid = FluidUtil.getFluidContained(box.item).orElse(FluidStack.EMPTY);

        if (!CMFluids.isMoltenMaterial(containedFluid.getFluid()))
            return;

        LadleEntity.spawnParticles(level, box.worldPosition.subtract(0, (1.3f + MINY_LADLE), 0));

        if (be.getSpeed() != 0) {
            RandomSource r = level.getRandom();
            Vec3 v = box.worldPosition.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                    .multiply(1f, 0, 1f));

            float speedFactor = 1f - (Math.abs(be.getSpeed()) / AllConfigs.server().kinetics.maxRotationSpeed.get());

            if (r.nextInt(5 + (int)(speedFactor * 25)) == 0)
                level.addParticle(ParticleTypes.FALLING_LAVA, v.x, v.y - 1.3f, v.z, 0, 0, 0);
        }
    }
}
