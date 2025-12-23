package fr.lucreeper74.createmetallurgy.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import dev.engine_room.flywheel.lib.transform.Translate;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItemRenderer;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ChainConveyorRenderer.class)
public class LadleRendererChainConveyorMixin {
    @WrapOperation(
            method = "renderBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            ),
            remap = false
    )
    private void renderFluid(SuperByteBuffer instance, PoseStack ms, VertexConsumer vertexConsumer, Operation<Void> original,
                             @Local(argsOnly = true) MultiBufferSource buffer,
                             @Local(argsOnly = true) ChainConveyorPackage box,
                             @Local(ordinal = 1) SuperByteBuffer boxBuffer,
                             @Local(ordinal = 1) int light) {
        if (boxBuffer == instance && box.item.getItem() instanceof LadleItem) {
            ms.pushPose();
            ms.mulPoseMatrix(instance.getTransforms().last().pose());
            ms.translate(Translate.CENTER, 0f, Translate.CENTER);
            LadleItemRenderer.renderFluidContents(box.item, -1, ms, buffer, light);
            ms.popPose();
        }

        original.call(instance, ms, vertexConsumer);
    }
}
