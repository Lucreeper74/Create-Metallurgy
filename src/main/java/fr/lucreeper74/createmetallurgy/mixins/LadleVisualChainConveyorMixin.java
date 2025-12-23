package fr.lucreeper74.createmetallurgy.mixins;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.Translate;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleFluidHandler;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.utils.CMFluidVisual;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChainConveyorVisual.class)
public class LadleVisualChainConveyorMixin {

    @Unique
    private CMFluidVisual CM$fluidVisual;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void ctor(VisualizationContext context, ChainConveyorBlockEntity blockEntity, float partialTick, CallbackInfo ci) {
        CM$fluidVisual = new CMFluidVisual(context, false, true);
    }

    @Inject(
            method = "beginFrame",
            at = @At("HEAD"),
            remap = false
    )
    private void begin(DynamicVisual.Context ctx, CallbackInfo ci) {
        CM$fluidVisual.begin();
    }

    @Inject(
            method = "_delete",
            at = @At("RETURN"),
            remap = false
    )
    private void delete(CallbackInfo ci) {
        CM$fluidVisual.delete();
    }

    @ModifyVariable(
            method = "setupBoxVisual",
            at = @At("STORE"),
            ordinal = 0,
            remap = false
    )
    private TransformedInstance[] modifyFluidVisuals(TransformedInstance[] original, @Local(argsOnly = true) ChainConveyorPackage box) {
        if (!(box.item.getItem() instanceof LadleItem))
            return original;

        FluidStack fluid = FluidUtil.getFluidContained(box.item).orElse(FluidStack.EMPTY);
        if (fluid.isEmpty()) return original;

        TransformedInstance[] buffers = CM$fluidVisual.setupBuffers(fluid, original.length);
        System.arraycopy(original, 0, buffers, 0, original.length);

        return buffers;
    }

    @SuppressWarnings("rawtypes")
    @WrapOperation(
            method = "setupBoxVisual",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;uncenter()Ldev/engine_room/flywheel/lib/transform/Translate;"
            ),
            remap = false
    )
    private Translate setupFluidVisual(TransformedInstance instance, Operation<Translate> original,
                                       @Local(ordinal = 0) TransformedInstance rigBuffer,
                                       @Local(ordinal = 1) TransformedInstance boxBuffer,
                                       @Local(ordinal = 2) TransformedInstance buf,
                                       @Share("fluidBufferIndex") LocalIntRef fluidBufferIndex,
                                       @Share("fluid") LocalRef<FluidStack> fluid) {
        if (buf == rigBuffer || buf == boxBuffer) return original.call(instance);

        CM$fluidVisual.setupBuffer(fluid.get(), LadleFluidHandler.LADLE_CAPACITY, buf, fluidBufferIndex.get(), 8f / 16, 8f / 16);
        fluidBufferIndex.set(fluidBufferIndex.get() + 1);

        return instance;
    }
}