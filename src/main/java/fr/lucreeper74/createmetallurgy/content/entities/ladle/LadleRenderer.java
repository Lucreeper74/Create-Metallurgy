package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class LadleRenderer extends EntityRenderer<LadleEntity> {

    public LadleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        shadowRadius = 0.5f;
    }

    @Override
    public void render(LadleEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        ItemStack box = entity.box;
        LadleItemRenderer.renderFluidContents(box, yaw, ms, buffer, light);
    }

    public static void renderBox(Entity entity, float yaw, PoseStack ms, MultiBufferSource buffer, int light,
                                 PartialModel model) {
        if (model == null)
            return;
        SuperByteBuffer sbb = CachedBuffers.partial(model, Blocks.AIR.defaultBlockState());
        sbb.translate(-.5, 0, -.5)
                .rotateCentered(-AngleHelper.rad(yaw + 90), Direction.UP)
                .light(light)
                .nudge(entity.getId());
        sbb.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    public ResourceLocation getTextureLocation(LadleEntity pEntity) {
        return null;
    }
}
