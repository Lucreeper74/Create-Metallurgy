package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.AngleHelper;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.address.NetworkAddressRenderer;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class LightBulbRenderer extends SafeBlockEntityRenderer<LightBulbBlockEntity> {

    public LightBulbRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(LightBulbBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        NetworkAddressRenderer.renderOnBlockEntity(be, ms, buffer, light, overlay);

        float glowValue = be.glow.getValue(partialTicks);

        int color = (int) ((210 / 15) * glowValue);
        float size = 1 + ((float) 1 / 30) * glowValue;


        BlockState blockState = be.getBlockState();
        TransformStack msr = TransformStack.cast(ms);

        Direction face = blockState.getOptionalValue(DisplayLinkBlock.FACING)
                .orElse(Direction.UP);
        if (face.getAxis()
                .isHorizontal())
            face = face.getOpposite();
        ms.pushPose();

        msr.centre()
                .rotateY(AngleHelper.horizontalAngle(face))
                .rotateX(-AngleHelper.verticalAngle(face) - 90)
                .unCentre();

        if (glowValue > .125f) {
            CachedBufferer.partial(CMPartialModels.BULB_INNER_GLOW, be.getBlockState())
                    .light(light)
                    .color(color, color, color, 255)
                    .disableDiffuse()
                    .translate(.5, .5625, .5)
                    .scale(size)
                    .translate(-0.5, -0.5625, -0.5)
                    .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));
            CachedBufferer.partial(CMPartialModels.BULB_TUBES.get(be.getColor()), blockState)
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.translucent()));
            CachedBufferer.partial(CMPartialModels.BULB_TUBES_GLOW.get(be.getColor()), blockState)
                    .light(light)
                    .color(color, color, color, 255)
                    .disableDiffuse()
                    .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));
        } else CachedBufferer.partial(CMPartialModels.BULB_TUBES.get(be.getColor()), blockState)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.translucent()));

        ms.popPose();
    }
}
