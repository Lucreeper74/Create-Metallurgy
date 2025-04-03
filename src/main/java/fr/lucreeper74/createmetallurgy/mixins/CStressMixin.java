package fr.lucreeper74.createmetallurgy.mixins;

import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CStress.class)
public class CStressMixin {
    @Inject(method = "assertFromCreate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onSetStress(BlockBuilder<?, ?> builder, CallbackInfo ci) {
        ci.cancel(); // Why?...
    }
}
