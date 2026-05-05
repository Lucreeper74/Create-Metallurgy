package fr.lucreeper74.createmetallurgy.mixins.chainconveyor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import com.simibubi.create.content.logistics.box.PackageItem;
import dev.engine_room.flywheel.lib.transform.Translate;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItemRenderer;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
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
    private void renderLadleFluid(SuperByteBuffer instance, PoseStack ms, VertexConsumer vertexConsumer,
                                  Operation<Void> original,
                                  @Local(argsOnly = true) MultiBufferSource buffer,
                                  @Local(argsOnly = true) ChainConveyorPackage box,
                                  @Local(name = "boxBuffer") SuperByteBuffer boxBuffer,
                                  @Local(name = "offset") Vec3 offset,
                                  @Local(name = "yaw") float yaw,
                                  @Local(name = "zRot") float zRot,
                                  @Local(name = "xRot") float xRot,
                                  @Local(name = "light") int light) {

        original.call(instance, ms, vertexConsumer);

        if (instance != boxBuffer || !(box.item.getItem() instanceof LadleItem))
            return;

        ms.pushPose();
        ms.translate(offset.x, offset.y + 10 / 16f, offset.z);
        ms.mulPose(new Quaternionf().rotateY((float) Math.toRadians(yaw)));
        ms.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(zRot)));
        ms.mulPose(new Quaternionf().rotateX((float) Math.toRadians(xRot)));
        ms.translate(-0.5, -0.5 - PackageItem.getHookDistance(box.item) + 7 / 16f, -0.5);
        ms.translate(Translate.CENTER, 0f, Translate.CENTER);
        LadleItemRenderer.renderFluidContents(box.item, -1, ms, buffer, light);
        ms.popPose();
    }
}
