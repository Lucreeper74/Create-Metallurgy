package fr.lucreeper74.createmetallurgy.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.FluidRenderHelper;
import net.createmod.catnip.render.PonderRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

@OnlyIn(Dist.CLIENT)
public class CMFluidRenderHelper {
    // Flowing fluid box
    public static void renderFlowingFluidBox(FluidStack fluid, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax,
                                             MultiBufferSource buffer, PoseStack ms, int light, boolean renderBottom, boolean invertGasses) {
        VertexConsumer builder = buffer.getBuffer(PonderRenderTypes.fluid());
        TextureAtlasSprite fluidTexture = getFlowingTextureOrMissing(fluid);
        int color = IClientFluidTypeExtensions.of(fluid.getFluidType()).getTintColor();
        int blockLightIn = (light >> 4) & 0xF;
        int luminosity = Math.max(blockLightIn, fluid.getFluidType().getLightLevel());
        light = (light & 0xF00000) | luminosity << 4;

        Vec3 center = new Vec3(xMin + (xMax - xMin) / 2, yMin + (yMax - yMin) / 2, zMin + (zMax - zMin) / 2);
        ms.pushPose();
        if (invertGasses && fluid.getFluidType().isLighterThanAir()) {
            ms.translate(center.x, center.y, center.z);
            ms.mulPose(Axis.XP.rotationDegrees(180));
            ms.translate(-center.x, -center.y, -center.z);
        }

        for (Direction side : Iterate.directions) {
            if (side == Direction.DOWN && !renderBottom)
                continue;

            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (side.getAxis()
                    .isHorizontal()) {
                if (side.getAxis() == Direction.Axis.X) {
                    renderFlowingTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin,
                            builder, ms, light, color, fluidTexture);
                } else {
                    renderFlowingTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin,
                            builder, ms, light, color, fluidTexture);
                }
            } else {
                renderFlowingTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin,
                        builder, ms, light, color, fluidTexture);
            }
        }

        ms.popPose();
    }

    public static void renderFlowingTiledFace(Direction dir, float left, float down, float right, float up,
                                              float depth, VertexConsumer builder, PoseStack ms, int light, int color, TextureAtlasSprite texture) {
        FluidRenderHelper.renderTiledFace(dir, left, down, right, up, depth, builder, ms, light, color, texture, .5f);
    }

    public static TextureAtlasSprite getFlowingTextureOrMissing(FluidStack fluid) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ext.getFlowingTexture(fluid));
    }

    public static void renderTrapezoidFluidBox(FluidStack fluidStack,
                                               float xMin_base, float xMax_base, float yMin, float zMin_base, float zMax_base,
                                               float xMin_top, float xMax_top, float yOffset, float zMin_top, float zMax_top,
                                               MultiBufferSource buffer, PoseStack ms, int light, boolean renderBottom, boolean invertGasses) {
        // Base (at yMin_base)
        Vec3 base00 = new Vec3(xMin_base, yMin, zMin_base);
        Vec3 base10 = new Vec3(xMax_base, yMin, zMin_base);
        Vec3 base11 = new Vec3(xMax_base, yMin, zMax_base);
        Vec3 base01 = new Vec3(xMin_base, yMin, zMax_base);

        // Top (at yMax_top)
        Vec3 top00 = new Vec3(xMin_top, yMin + yOffset, zMin_top);
        Vec3 top10 = new Vec3(xMax_top, yMin + yOffset, zMin_top);
        Vec3 top11 = new Vec3(xMax_top, yMin + yOffset, zMax_top);
        Vec3 top01 = new Vec3(xMin_top, yMin + yOffset, zMax_top);

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite stillTexture = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(clientFluid.getStillTexture(fluidStack));

        int color = clientFluid.getTintColor(fluidStack);
        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

        ms.pushPose();
        CMFluidRenderHelper.quad(builder, ms, base10, base00, top00, top10, color, stillTexture, light); // normal ~ Direction.NORTH
        CMFluidRenderHelper.quad(builder, ms, base01, base11, top11, top01, color, stillTexture, light); // normal ~ Direction.SOUTH
        CMFluidRenderHelper.quad(builder, ms, base00, base01, top01, top00, color, stillTexture, light); // normal ~ Direction.WEST
        CMFluidRenderHelper.quad(builder, ms, base11, base10, top10, top11, color, stillTexture, light); // normal ~ Direction.EAST
        CMFluidRenderHelper.quad(builder, ms, top00, top01, top11, top10, color, stillTexture, light); // Top face
        if (renderBottom)
            CMFluidRenderHelper.quad(builder, ms, base00, base01, base11, base10, color, stillTexture, light); // Bottom face
        ms.popPose();
    }

    // Quad stuff
    public static void quad(VertexConsumer builder, PoseStack ms,
                            Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                            int color, TextureAtlasSprite sprite, int light) {
        // Compute normal with vectorial product (for inclined faces)
        Vec3 edge1 = p2.subtract(p1);
        Vec3 edge2 = p3.subtract(p1);
        Vec3 normal = edge1.cross(edge2).normalize();

        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        putVertex(builder, ms, p1, color, u0, v0, normal, light);
        putVertex(builder, ms, p2, color, u1, v0, normal, light);
        putVertex(builder, ms, p3, color, u1, v1, normal, light);
        putVertex(builder, ms, p4, color, u0, v1, normal, light);
    }

    public static void putVertex(VertexConsumer builder, PoseStack ms, Vec3 pos, int color,
                                 float u, float v, Vec3 normal, int light) {
        PoseStack.Pose peek = ms.last();
        int a = color >> 24 & 0xff, r = color >> 16 & 0xff, g = color >> 8 & 0xff, b = color & 0xff;

        builder.addVertex(peek.pose(), (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setLight(light)
                .setNormal(peek.copy(), (float) normal.x, (float) normal.y, (float) normal.z);
    }

    // Colored FluidBox
    public static int RGBAtoColor(int r, int g, int b, int a) {
        return (b) | (g << 8) | (r << 16) | (a << 24);
    }

    public static void renderFluidBox(FluidStack fluidStack, float xMin, float yMin, float zMin, float xMax, float yMax,
                                      float zMax, MultiBufferSource buffer, PoseStack ms, int light, int color, boolean renderBottom) {
        renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, FluidRenderHelper.getFluidBuilder(buffer), ms, light, color,
                renderBottom);
    }

    public static void renderFluidBox(FluidStack fluidStack, float xMin, float yMin, float zMin, float xMax, float yMax,
                                      float zMax, VertexConsumer builder, PoseStack ms, int light, int color, boolean renderBottom) {
        if (fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
        FluidType fluidAttributes = fluid.getFluidType();
        TextureAtlasSprite fluidTexture = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(clientFluid.getStillTexture(fluidStack));
        int blockLightIn = (light >> 4) & 0xF;
        int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
        light = (light & 0xF00000) | luminosity << 4;

        Vec3 center = new Vec3(xMin + (xMax - xMin) / 2, yMin + (yMax - yMin) / 2, zMin + (zMax - zMin) / 2);
        ms.pushPose();
        if (fluidAttributes.isLighterThanAir())
            TransformStack.of(ms)
                    .translate(center)
                    .rotateX(180)
                    .translateBack(center);

        for (Direction side : Iterate.directions) {
            if (side == Direction.DOWN && !renderBottom)
                continue;

            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (side.getAxis().isHorizontal()) {
                if (side.getAxis() == Direction.Axis.X)
                    FluidRenderHelper.renderTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, ms, light,
                            color, fluidTexture, 1f);
                else
                    FluidRenderHelper.renderTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, ms, light,
                            color, fluidTexture, 1f);
            } else
                FluidRenderHelper.renderTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, ms, light, color,
                        fluidTexture, 1f);
            ms.popPose();
        }
    }
}
