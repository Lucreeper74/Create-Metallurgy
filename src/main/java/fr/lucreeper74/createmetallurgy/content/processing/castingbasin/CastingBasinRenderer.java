package fr.lucreeper74.createmetallurgy.content.processing.castingbasin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CastingBasinRenderer extends SmartBlockEntityRenderer<CastingBasinBlockEntity> {
    public CastingBasinRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CastingBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        //Render Fluids
        SmartFluidTankBehaviour tank = be.inputTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level > 0.01F) {

            float min = 2f / 16f;
            float max = min + (12f / 16f);
            float yOffset = (11 / 16f) * level;

            ms.pushPose();
            ms.translate(0, yOffset, 0);

            FluidRenderer.renderFluidBox(fluidStack,
                    min, min - yOffset, min,
                    max, min, max,
                    buffer, ms, light, false);

            ms.popPose();
        }

        //Render Items
        ms.pushPose();
        ms.translate(0.5f, 0f, 0.5f);
        ms.scale(3f, 3f, 3f);

        ItemStack stack = be.inv.getItem(0);
        renderItem(ms, buffer, light, overlay, stack);
        ms.popPose();
        }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, ms, buffer, 0);
    }
}