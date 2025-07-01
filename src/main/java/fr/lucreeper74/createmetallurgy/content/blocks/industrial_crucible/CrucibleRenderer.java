package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryTank;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
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

    public CrucibleRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.isController())
            return;
        if (be.foundry.isActive()) {
            renderAsController(be, partialTicks, ms, buffer, light, overlay);
        }

        renderItems(be, partialTicks, ms, buffer, light, overlay);

        float capHeight = 3 / 16f;
        float tankHullWidth = 3 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        float clampedLevel;

        float yMin = capHeight + minPuddleHeight; // Initial yMin value

        FoundryTank tank = be.tankInventory;
        for (FluidStack fluidStack : tank.fluids) {

            float level = (float) fluidStack.getAmount() / be.tankInventory.getCapacity();

            if (level < 1 / (512f * totalHeight))
                return;
            clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

            if (fluidStack.isEmpty())
                return;

            boolean top = fluidStack.getFluid()
                    .getFluidType()
                    .isLighterThanAir();

            float xMin = tankHullWidth;
            float xMax = xMin + be.width - 2 * tankHullWidth;
            float yMax = yMin + clampedLevel;

            if (top) {
                yMin += totalHeight - clampedLevel;
                yMax += totalHeight - clampedLevel;
            }

            float zMin = tankHullWidth;
            float zMax = zMin + be.width - 2 * tankHullWidth;

            ms.pushPose();
            ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, buffer, ms, light, false, true);
            ms.popPose();

            yMin = yMax; // To stack fluids upwards
        }
    }

    protected void renderAsController(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                      int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ms.pushPose();
        TransformStack<PoseTransformStack> msr = TransformStack.of(ms);
        msr.translate(be.width / 2f, .5f, be.width / 2f);

        float dialPivotY = 6.5f / 16f;
        float dialPivotZ = 8f / 16f;
        float progress = Mth.clamp(be.foundry.gauge.getValue(partialTicks) + .5f, 0, 1.05f);

        Random random = new Random();

        if (progress >= 1f) {
            // Make the gauge to jiggle
            progress += Mth.clamp(random.nextFloat(.02f) - .01f, -.01f, .01f);
        }

        for (Direction d : Iterate.horizontalDirections) {
            ms.pushPose();
            float yRot = -d.toYRot() - 90;
            CachedBuffers.partial(CMPartialModels.THERMOMETER_GAUGE, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.width / 2f - 6 / 16f, 0, 0)
                    .light(light)
                    .renderInto(ms, vb);
            CachedBuffers.partial(CMPartialModels.THERMOMETER_DIAL, blockState)
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.width / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-180 * progress)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(light)
                    .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }

    protected void renderItems(CrucibleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, int overlay) {
        int tankIndex = 0;
        CrucibleBlockEntity controllerBE = be.getControllerBE();

        for (int yOffset = 0; yOffset < be.height; yOffset++) {
            for (int xOffset = 0; xOffset < be.width; xOffset++) {
                for (int zOffset = 0; zOffset < be.width; zOffset++) {

                    ItemStack stack = controllerBE.foundry.inputInv.getStackInSlot(tankIndex);

                    if (!stack.isEmpty()) {
                        ms.pushPose();
                        if (stack.getItem() instanceof BlockItem) {
                            ms.translate(xOffset + .5f, yOffset + .1f, zOffset + .5f);
                            ms.scale(2.5f, 2.5f, 2.5f);
                        } else
                            ms.translate(xOffset + .5f, yOffset + .5f, zOffset + .5f);

                        Minecraft mc = Minecraft.getInstance();
                        mc.getItemRenderer()
                                .renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
                        ms.popPose();
                    }
                    tankIndex++;
                }
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CrucibleBlockEntity be) {
        return be.isController();
    }
}
