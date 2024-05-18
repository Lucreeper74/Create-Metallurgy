package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import fr.lucreeper74.createmetallurgy.content.redstone.lightbulb.network.address.NetworkAddressRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class LightBulbRenderer extends SafeBlockEntityRenderer<LightBulbBlockEntity> {

    public LightBulbRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(LightBulbBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        NetworkAddressRenderer.renderOnBlockEntity(be, ms, bufferSource, light, overlay);
    }
}
