package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryTank;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import fr.lucreeper74.createmetallurgy.utils.SideAttachment;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

public class CrucibleRenderer extends SafeBlockEntityRenderer<CrucibleBlockEntity> {

    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        renderItem(be, partialTicks, ms, buffer, light, overlay);
        renderAttachments(be, partialTicks, ms, buffer, light, overlay);

        if (!be.isController())
            return;

        float capHeight = 3 / 16f;
        float tankHullWidth = 3 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        float xMax = tankHullWidth + be.width - 2 * tankHullWidth;

        float yMin = capHeight + minPuddleHeight; // Initial yMin value

        float zMax = tankHullWidth + be.width - 2 * tankHullWidth;

        FoundryTank tank = be.tankInventory;

        for (FoundryTank.FoundryTankSegment segment : tank.segments) {
            FluidStack fluidStack = segment.getFluid();

            if (fluidStack.isEmpty())
                continue;

            float fluidLevel = segment.getFluidLevel().getValue(partialTicks);
            float clampedFluidHeight = Mth.clamp((fluidLevel * totalHeight), 0, totalHeight); // Cap for safety

            float yMax = clampedFluidHeight + yMin;


            boolean top = fluidStack.getFluid()
                    .getFluidType()
                    .isLighterThanAir();

            if (top) {
                // Not sure of this behavior
                yMin += totalHeight - clampedFluidHeight;
                yMax += totalHeight - clampedFluidHeight;
            }

            ms.pushPose();
            ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, tankHullWidth, yMin, tankHullWidth, xMax, yMax, zMax, buffer, ms, light, false, true);
            ms.popPose();

            yMin = yMax; // To stack fluids upwards
        }
    }

    protected void renderItem(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {

        ItemStack stack = be.foundrySlot.getStack();

        if (!stack.isEmpty()) {
            ms.pushPose();
            if (stack.getItem() instanceof BlockItem) {
                ms.translate(.5f, .1f, .5f);
                ms.scale(2.5f, 2.5f, 2.5f);
            } else
                ms.translate(.5f, .5f, .5f);

            Minecraft mc = Minecraft.getInstance();
            mc.getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
            ms.popPose();
        }
    }

    protected void renderAttachments(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                     int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ms.pushPose();

        for (Direction side : Iterate.directions) {
            float yRot = -side.toYRot() - 90;
            float zRot = side.equals(Direction.UP) ? 90
                    : side.equals(Direction.DOWN) ? -90 : 0;

            PartialModel attachmentModel = be.getSideAttachment(side).getModel();

            if (attachmentModel == null)
                continue;

            if (be.getSideAttachment(side).equals(SideAttachment.GAUGE))
                renderGauge(be, attachmentModel, yRot, partialTicks, ms, buffer, light);
            else {

                ms.pushPose();
                CachedBuffers.partial(attachmentModel, blockState)
                        .rotateYCenteredDegrees(yRot)
                        .rotateZCenteredDegrees(zRot)
                        .light(light)
                        .renderInto(ms, vb);
                ms.popPose();
            }
        }
        ms.popPose();
    }

    protected void renderGauge(CrucibleBlockEntity be, PartialModel gaugeModel, float yRot, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        CrucibleBlockEntity controller = be.getControllerBE();
        if (controller == null)
            return;

        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ms.pushPose();
        TransformStack<PoseTransformStack> msr = TransformStack.of(ms);
        msr.translate(.5f, .5f, .5f);

        float dialPivotY = 6.5f / 16f;
        float dialPivotZ = 8f / 16f;
        float progress = Mth.clamp(controller.foundryData.gauge.getValue(partialTicks) + .25f, 0, 1.05f);

        Random random = new Random();

        if (progress >= 1f) {
            // Make the gauge to jiggle
            float amplitude = .015f;
            progress += Mth.clamp(random.nextFloat(.02f) - amplitude, -amplitude, amplitude);
        }

        CachedBuffers.partial(gaugeModel, blockState)
                .rotateYDegrees(yRot)
                .uncenter()
                .translate(1 / 16f, 0, 0)
                .light(light)
                .renderInto(ms, vb);
        CachedBuffers.partial(CMPartialModels.THERMOMETER_DIAL, blockState)
                .rotateYDegrees(yRot)
                .uncenter()
                .translate(1 / 16f, 0, 0)
                .translate(0, dialPivotY, dialPivotZ)
                .rotateXDegrees(-180 * progress)
                .translate(0, -dialPivotY, -dialPivotZ)
                .light(light)
                .renderInto(ms, vb);

        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(CrucibleBlockEntity be) {
        return be.isController();
    }
}
