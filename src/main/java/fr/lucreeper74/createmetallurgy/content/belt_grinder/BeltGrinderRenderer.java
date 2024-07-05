package fr.lucreeper74.createmetallurgy.content.belt_grinder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import fr.lucreeper74.createmetallurgy.registries.CMSpriteShifts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BeltGrinderRenderer extends SafeBlockEntityRenderer<BeltGrinderBlockEntity> {
    public BeltGrinderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BeltGrinderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderShaft(be, ms, buffer, light, overlay);
        renderBelt(be, ms, buffer, light);
        renderItems(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderBelt(BeltGrinderBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SpriteShiftEntry beltShift = CMSpriteShifts.SAND_PAPER_BELT;
        float speed = be.getSpeed() * 2;
        float renderTick = AnimationTickHolder.getRenderTime(be.getLevel());
        float time = renderTick * Direction.AxisDirection.POSITIVE.getStep();

        float spriteSize = beltShift.getTarget()
                .getV1()
                - beltShift.getTarget()
                .getV0();

        double scroll = speed * time / (31.5 * 16);
        scroll = scroll - Math.floor(scroll);
        scroll = scroll * spriteSize * .5f;

        SuperByteBuffer rotatedCoil = CachedBufferer.partialFacing(CMPartialModels.GRINDER_BELT, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        rotatedCoil.light(light)
                .renderInto(ms, vb);

        rotatedCoil.shiftUVScrolling(beltShift, (float) scroll)
                .light(light)
                .renderInto(ms, vb);
    }

    protected void renderItems(BeltGrinderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, int overlay) {
        if (!be.inv.isEmpty()) {
            boolean alongZ = be.getBlockState().getValue(BeltGrinderBlock.HORIZONTAL_FACING).getAxis() == Direction.Axis.Z;
            ms.pushPose();

            float offset = getOffset(be, partialTicks, alongZ);

            for (int i = 0; i < be.inv.getSlots(); i++) {
                ItemStack stack = be.inv.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;

                Minecraft mc = Minecraft.getInstance();
                ItemRenderer itemRenderer = mc.getItemRenderer();
                BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
                boolean blockItem = modelWithOverrides.isGui3d();

                ms.translate(alongZ ? offset : .5, blockItem ? .925f : 13f / 16f, alongZ ? .5 : offset);

                ms.scale(.5f, .5f, .5f);
                if (alongZ)
                    ms.mulPose(Axis.YP.rotationDegrees(90));
                ms.mulPose(Axis.XP.rotationDegrees(90));
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, mc.level, 0);
                break;
            }

            ms.popPose();
        }
    }

    private void renderShaft(BeltGrinderBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light,
                             int overlay) {
        KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be), ms,
                buffer.getBuffer(RenderType.solid()), light);
    }

    private SuperByteBuffer getRotatedModel(BeltGrinderBlockEntity be) {
        return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                getRenderedBlockState(be));
    }

    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
    }

    private static float getOffset(BeltGrinderBlockEntity be, float partialTicks, boolean alongZ) {
        boolean moving = be.inv.recipeDuration != 0;
        float offset = moving ? (be.inv.remainingTime) / be.inv.recipeDuration : 0;
        float processingSpeed = Mth.clamp(Math.abs(be.getSpeed()) / 32, 1, 128);
        if (moving) {
            offset = Mth
                    .clamp(offset + ((-partialTicks + .5f) * processingSpeed) / be.inv.recipeDuration, 0.125f, 1f);
            if (!be.inv.appliedRecipe)
                offset += 1;
            offset /= 2;
        }

        if (be.getSpeed() == 0)
            offset = .5f;
        if (be.getSpeed() < 0 ^ alongZ)
            offset = 1 - offset;
        return offset;
    }
}