package fr.lucreeper74.createmetallurgy.mixins;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class FoundryBasinCheckMixin extends KineticBlockEntity {

    public FoundryBasinCheckMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "getBasin", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void createmetallurgy$isBasinExceptFoundry(CallbackInfoReturnable<Optional<BasinBlockEntity>> cir) {
        if (level == null) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        BlockEntity basinBE = level.getBlockEntity(worldPosition.below(2));
        if (!(basinBE instanceof BasinBlockEntity) || basinBE instanceof FoundryBasinBlockEntity) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        cir.setReturnValue(Optional.of((BasinBlockEntity) basinBE));
    }
}
