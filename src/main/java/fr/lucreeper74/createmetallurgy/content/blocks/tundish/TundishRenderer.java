package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import fr.lucreeper74.createmetallurgy.utils.CMFluidRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class TundishRenderer extends SmartBlockEntityRenderer<TundishBlockEntity> {

    public TundishRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TundishBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderFluid(be, partialTicks, ms, buffer, light);
    }

    protected void renderFluid(TundishBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float yMin = 3f / 16f;
            float yOffset = (8 / 16f) * level;

            BlockState state = be.getBlockState();
            boolean alongZaxis = state.getValue(TundishBlock.ALONG_Z_AXIS);
            boolean front = state.getValue(TundishBlock.FRONT);
            boolean rear = state.getValue(TundishBlock.REAR);

            // [ trapezoid faces ]
            // --- base (rectangle depending on oriented axis)
            // coordinates along Z axis
            float minSide_base = 5 / 16f;
            float minEdge_base = rear ? 0f : 2 / 16f;
            float maxSide_base = 11 / 16f;
            float maxEdge_base = front ? 1f : 14 / 16f;
            // coordinates rotation (90°)
            float xMin_base = alongZaxis ? minSide_base : minEdge_base;
            float zMin_base = alongZaxis ? minEdge_base : minSide_base;
            float xMax_base = alongZaxis ? maxSide_base : maxEdge_base;
            float zMax_base = alongZaxis ? maxEdge_base : maxSide_base;

            // --- top (square)
            // coordinates along Z axis
            float minSide_top = 2 / 16f + (1-level) * 1/16f;
            float minEdge_top = rear ? 0f : 2 / 16f;
            float maxSide_top = 13 / 16f + level * 1/16f;
            float maxEdge_top = front ? 1f : 14 / 16f;
            // coordinates rotation (90°)
            float xMin_top = alongZaxis ? minSide_top : minEdge_top;
            float zMin_top = alongZaxis ? minEdge_top : minSide_top;
            float xMax_top = alongZaxis ? maxSide_top : maxEdge_top;
            float zMax_top = alongZaxis ? maxEdge_top : maxSide_top;

            // TODO: move vec3 stuff to CMFluidHelper
            // Base (à yMin_base)
            Vec3 base00 = new Vec3(xMin_base, yMin, zMin_base);
            Vec3 base10 = new Vec3(xMax_base, yMin, zMin_base);
            Vec3 base11 = new Vec3(xMax_base, yMin, zMax_base);
            Vec3 base01 = new Vec3(xMin_base, yMin, zMax_base);

            // Top (à yMax_top)
            Vec3 top00 = new Vec3(xMin_top, yMin+yOffset, zMin_top);
            Vec3 top10 = new Vec3(xMax_top, yMin+yOffset, zMin_top);
            Vec3 top11 = new Vec3(xMax_top, yMin+yOffset, zMax_top);
            Vec3 top01 = new Vec3(xMin_top, yMin+yOffset, zMax_top);

            Fluid fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
            TextureAtlasSprite stillTexture = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(clientFluid.getStillTexture(fluidStack));

            int color = clientFluid.getTintColor(fluidStack);
            VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

            ms.pushPose();
            CMFluidRenderHelper.quad(builder, ms, base10, base00, top00, top10, color, stillTexture, light); // normal ~ Direction.NORTH

            // Face arrière (Z max)
            CMFluidRenderHelper.quad(builder, ms, base01, base11, top11, top01, color, stillTexture, light); // normal ~ Direction.SOUTH

            // Face gauche (X min)
            CMFluidRenderHelper.quad(builder, ms, base00, base01, top01, top00, color, stillTexture, light); // normal ~ Direction.WEST

            // Face droite (X max)
            CMFluidRenderHelper.quad(builder, ms, base11, base10, top10, top11, color, stillTexture, light); // normal ~ Direction.EAST

            // Top face
            CMFluidRenderHelper.quad(builder, ms, top00, top01, top11, top10, color, stillTexture, light);
            ms.popPose();

//            if (yOffset >= yOffsetMax) {
//                ms.pushPose();
//                ms.translate(0, Math.min(yOffset, yOffsetMax), 0);
//                NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, min, yMin - yOffset, min, max, yMin,
//                        max, buffer, ms, light, false, false);
//                ms.popPose();
//            }

//            ms.pushPose();
//            ms.translate(0, Math.min(yOffset, yTrapezoidOffsetMax), 0);
//            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin_top, yMin - yOffset, zMin_top, xMax_top, yMin,
//                    zMax_top, buffer, ms, light, false, false);
//            ms.popPose();

//            FluidRenderHelper.renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin,
//                    builder, ms, light, color, fluidTexture);

//            renderTopFluidLayer(fluidStack, yOffset, yTrapezoidOffsetMax, topMin, topMax, ms, buffer, light);
        }
    }


    private void renderTopFluidLayer(FluidStack fluidStack, float yOffset, float yMin, float min, float max,
                                     PoseStack ms, MultiBufferSource buffer, int light) {
        if (yOffset >= yMin) {
            float yLayerOffset = yOffset - yMin;

            ms.pushPose();
            ms.translate(0, yMin - 3/16f, 0);
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, min, yMin - yLayerOffset, min, max, yMin,
                    max, buffer, ms, light, false, false);
            ms.popPose();
        }
    }
}