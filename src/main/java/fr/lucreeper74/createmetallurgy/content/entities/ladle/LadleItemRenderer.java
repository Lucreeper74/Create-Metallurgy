package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Optional;

public class LadleItemRenderer extends CustomRenderedItemModelRenderer {

    public void render(ItemStack box, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext displayContext, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        ms.pushPose();
        ms.translate(0f, -8 / 16f, 0f); // Avoid to have the fluid way to high in air ??
        renderFluidContents(box, -1, ms, buffer, light);
        ms.popPose();
    }

    public static void renderFluidContents(ItemStack box, float yaw, PoseStack ms, MultiBufferSource buffer, int light) {
        Optional<FluidStack> containedFluid = FluidUtil.getFluidContained(box);

        if (containedFluid.isEmpty() || containedFluid.get().isEmpty()) return;

        float fluidLevel = containedFluid.get().getAmount();

        float maxHeight = 10 / 16f;
        float hullWidth = 1 / 128f;
        float minPuddleHeight = 1 / 32f;
        float totalHeight = maxHeight - minPuddleHeight;
        float tankWidth = .5f;

        float level = fluidLevel / LadleFluidHandler.LADLE_CAPACITY * totalHeight;

        if (level == 0) return;

        boolean top = containedFluid.get().getFluid()
                .getFluidType()
                .isLighterThanAir();

        float xMin = 0;
        float xMax = xMin + tankWidth - 2 * hullWidth;
        float yMin = maxHeight + minPuddleHeight - level;
        float yMax = yMin + level;

        if (top) {
            yMin += totalHeight - level;
            yMax += totalHeight - level;
        }

        float zMin = 0;
        float zMax = zMin + tankWidth - 2 * hullWidth;

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(-yaw));
        ms.translate(-xMax / 2, level - totalHeight, -zMax / 2);
        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(containedFluid.get(), xMin, yMin, zMin, xMax, yMax, zMax,
                buffer, ms, light, false, true);
        ms.popPose();
    }
}
