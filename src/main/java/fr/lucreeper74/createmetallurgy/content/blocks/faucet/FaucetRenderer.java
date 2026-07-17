package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;

public class FaucetRenderer extends SafeBlockEntityRenderer<FaucetBlockEntity> {

    public FaucetRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(FaucetBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (be.getBlockState().getValue(FaucetBlock.OPEN)) {
            FluidStack fluidStack = be.getRenderedFluid();

            Direction facing = be.getBlockState().getValue(FaucetBlock.FACING);
            if (!fluidStack.isEmpty()) {
                ms.pushPose();
                if (facing.getAxis().isHorizontal() && facing != Direction.NORTH) {
                    ms.translate(.5f, 0, .5f);
                    ms.mulPose(Axis.YP.rotationDegrees(-90f * (facing.getOpposite().get2DDataValue())));
                    ms.translate(-.5f, 0, -.5f);
                }

                // For the fluid in the faucet
                if (facing != Direction.DOWN)
                    NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, 5/16f, 7/16f, 8/16f, 11/16f, 10/16f, 15/16f, bufferSource, ms, light, false, true);

                // For the fluid stream
                Level level = be.getLevel();
                BlockPos targetPos = be.getBlockPos().below(be.getFallingDistance());
                VoxelShape shape = level.getBlockState(targetPos).getShape(level, targetPos);
                double maxY = 0;
                if (!shape.isEmpty())
                    maxY = shape.bounds().maxY;

                AABB bb = new AABB(11/16f, 10/16f, 8/16f,
                        5/16f, maxY - (be.getFallingDistance() + .5f), 6/16f);
                NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, (float) bb.minX, (float) bb.minY, (float) bb.minZ,
                        (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, bufferSource, ms, light, true, true);

                ms.popPose();
            }
        }
    }
}