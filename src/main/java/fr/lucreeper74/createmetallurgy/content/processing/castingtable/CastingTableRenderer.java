package fr.lucreeper74.createmetallurgy.content.processing.castingtable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static fr.lucreeper74.createmetallurgy.content.processing.castingtable.CastingTableBlock.FACING;

public class CastingTableRenderer extends SmartBlockEntityRenderer<CastingTableBlockEntity> {
    public CastingTableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CastingTableBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
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

            float min = 13f / 16f;
            float yOffset = (.75f / 16f) * level;

            ms.pushPose();
            ms.translate(0, yOffset, 0);

            FluidRenderer.renderFluidBox(fluidStack,
                    2f / 16f, min - yOffset, 2f / 16f,
                    14f / 16f, min, 14f / 16f,
                    buffer, ms, light, false);

            ms.popPose();
        }

        //Render Items
        ms.pushPose();

        ms.translate(.5f, 0, .5f);
        ms.mulPose(Vector3f.YP.rotationDegrees(90f * (be.getBlockState().getValue(FACING).get2DDataValue())));
        ms.translate(-.5f, 0, -.5f);

        ms.translate(.5f, 13.5f / 16f, 5 / 16f);
        ms.scale(1.5f, 1.5f, 1.5f);
        ms.mulPose(Vector3f.XP.rotationDegrees(90f));

        renderItem(ms, buffer, light, overlay, be.inv.getItem(0));
        renderItem(ms, buffer, light, overlay, be.moldInv.getItem(0));
        ms.popPose();
    }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, ms, buffer, 0);
    }
}