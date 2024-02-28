package fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalBeltGrinderRenderer extends SafeBlockEntityRenderer<MechanicalBeltGrinderBlockEntity> {
    public MechanicalBeltGrinderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(MechanicalBeltGrinderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderShaft(be, ms, buffer, light, overlay);
    }

    private void renderShaft(MechanicalBeltGrinderBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be), ms,
                buffer.getBuffer(RenderType.solid()), light);
    }

    private SuperByteBuffer getRotatedModel(MechanicalBeltGrinderBlockEntity be) {
        return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                getRenderedBlockState(be));
    }
    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
    }
}