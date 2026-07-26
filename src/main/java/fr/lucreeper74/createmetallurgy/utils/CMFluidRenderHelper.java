package fr.lucreeper74.createmetallurgy.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.FluidRenderHelper;
import net.createmod.catnip.render.PonderRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

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
}
