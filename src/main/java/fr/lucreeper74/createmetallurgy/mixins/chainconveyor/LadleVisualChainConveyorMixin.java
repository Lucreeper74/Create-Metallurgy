package fr.lucreeper74.createmetallurgy.mixins.chainconveyor;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.Translate;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleFluidVisual;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChainConveyorVisual.class)
public class LadleVisualChainConveyorMixin {

    @Unique
    private LadleFluidVisual CM$ladleFluidVisual;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void ctor(VisualizationContext context, ChainConveyorBlockEntity blockEntity, float partialTick, CallbackInfo ci) {
        CM$ladleFluidVisual = new LadleFluidVisual(context);
    }

    @Inject(
            method = "beginFrame",
            at = @At("HEAD"),
            remap = false
    )
    private void begin(DynamicVisual.Context ctx, CallbackInfo ci) {
        CM$ladleFluidVisual.begin();
    }

    @Inject(
            method = "beginFrame",
            at = @At("RETURN"),
            remap = false
    )
    private void end(CallbackInfo ci) {
        CM$ladleFluidVisual.end();
    }

    @Inject(
            method = "_delete",
            at = @At("RETURN"),
            remap = false
    )
    private void delete(CallbackInfo ci) {
        CM$ladleFluidVisual.delete();
    }

    @Definition(id = "TransformedInstance", type = TransformedInstance.class)
    @Expression("new TransformedInstance[]{?,?}")
    @ModifyExpressionValue(
            method = "setupBoxVisual",
            at = @At("MIXINEXTRAS:EXPRESSION"),
            remap = false
    )
    private TransformedInstance[] setupFluidBuffers(TransformedInstance[] original,
                                                    @Local(argsOnly = true) ChainConveyorPackage box,
                                                    @Share("fluid") LocalRef<FluidStack> fluid) {
        if (!(box.item.getItem() instanceof LadleItem))
            return original;

        fluid.set(FluidUtil.getFluidContained(box.item).orElse(FluidStack.EMPTY));

        if (fluid.get().isEmpty())
            return original;

        TransformedInstance instance = CM$ladleFluidVisual.setupInstance(fluid.get());


        TransformedInstance[] newOriginal = new TransformedInstance[original.length + 1];
        System.arraycopy(original, 0, newOriginal, 0, original.length);
        newOriginal[original.length] = instance;

        return newOriginal;
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
                                       @Share("fluid") LocalRef<FluidStack> fluid) {
        if (buf == rigBuffer || buf == boxBuffer)
            return original.call(instance);

        CM$ladleFluidVisual.render(fluid.get(), LadleItem.LADLE_CAPACITY, buf, 8f / 16);

        return instance;
    }
}