package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.lucreeper74.createmetallurgy.utils.CMFluidRenderHelper;
import net.createmod.catnip.animation.AnimationFunctions;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Random;

import static com.simibubi.create.content.logistics.box.PackageItem.isPackage;
import static fr.lucreeper74.createmetallurgy.content.blocks.tundish.TundishBlock.AXIS;
import static fr.lucreeper74.createmetallurgy.content.blocks.tundish.TundishBlockEntity.PROCESSING_TIME;
import static fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem.isLadle;

public class TundishRenderer extends SmartBlockEntityRenderer<TundishBlockEntity> {

    public TundishRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TundishBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        int processingTicks = be.processingTicks;
        float progress = 0;
        if (processingTicks != PROCESSING_TIME)
            progress = 1 - (processingTicks - partialTicks) / PROCESSING_TIME;
        progress = Mth.clamp(progress, 0, 1);

        renderFluid(be, partialTicks, ms, buffer, light, progress);
        renderItem(be, partialTicks, ms, buffer, light, overlay, progress);
    }

    protected void renderItem(TundishBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay, float drainProgress) {
        TransportedItemStack transported = be.heldItem;
        if (transported == null)
            return;

        var msr = TransformStack.of(ms);
        Vec3 itemPosition = VecHelper.getCenterOf(be.getBlockPos());

        Direction insertedFrom = transported.insertedFrom;
        if (!insertedFrom.getAxis()
                .isHorizontal())
            return;

        ms.pushPose();
        ms.translate(.5f, 15 / 16f, .5f);
        msr.nudge(0);
        float offset = Mth.lerp(partialTicks, transported.prevBeltPosition, transported.beltPosition);

        Vec3 offsetVec = Vec3.atLowerCornerOf(insertedFrom.getOpposite()
                        .getNormal()).scale(.5f - offset);
        ms.translate(0, offsetVec.y + .25f, 0);
        boolean alongX = insertedFrom.getClockWise()
                .getAxis() == Direction.Axis.X;

        ItemStack itemStack = transported.stack;
        Random r = new Random(0);
        ItemRenderer itemRenderer = Minecraft.getInstance()
                .getItemRenderer();
        int count = Mth.log2((itemStack.getCount())) / 2;
        boolean renderUpright = BeltHelper.isItemUpright(itemStack);
        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
        boolean blockItem = bakedModel.isGui3d();

        if (renderUpright)
            ms.translate(0, 3 / 32d, 0);

        float verticalAngle = getLadleAngle(offset, drainProgress, insertedFrom, be);

        Direction.Axis tundishAxis = be.getBlockState().getValue(AXIS);
        if (tundishAxis == Direction.Axis.Z)
            msr.rotateZDegrees(-verticalAngle);
        else
            msr.rotateXDegrees(verticalAngle);

        if (renderUpright) {
            Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 vectorForOffset = itemPosition.add(offsetVec);
            Vec3 diff = vectorForOffset.subtract(cameraPosition);

            if (insertedFrom.getAxis() == Direction.Axis.Z)
                diff = VecHelper.rotate(diff, verticalAngle, Direction.Axis.X);
            else
                diff = VecHelper.rotate(diff, -verticalAngle, Direction.Axis.Z);

            float yRot = (float) Mth.atan2(diff.z, -diff.x);
            ms.mulPose(Axis.YP.rotation((float) (yRot - Math.PI / 2)));
            ms.translate(0, 0, -1 / 16f);
        }

        for (int i = 0; i <= count; i++) {
            ms.pushPose();
            if (blockItem)
                ms.translate(r.nextFloat() * .0625f * i, 0, r.nextFloat() * .0625f * i);

            if (isLadle(itemStack) || isPackage(itemStack))
                ms.scale(1.5f, 1.5f, 1.5f);
            else
                ms.scale(.5f, .5f, .5f);

            if (!blockItem && !renderUpright)
                msr.rotateXDegrees(90);

            if (alongX)
                msr.rotateYDegrees(90);
            itemRenderer.render(itemStack, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
            ms.popPose();

            if (!renderUpright) {
                if (!blockItem)
                    msr.rotateYDegrees(10);
                ms.translate(0, blockItem ? 1 / 64d : 1 / 16d, 0);
            } else
                ms.translate(0, 0, -1 / 16f);
        }
        ms.popPose();
    }

    private static float getLadleAngle(float offset, float drainProgress, Direction insertedFrom, TundishBlockEntity be) {
        int start_angle = 0;
        int end_angle = 135;

        float verticalAngle;
        if (offset < 0.5f) {
            float localT = offset / 0.5f;
            verticalAngle = Mth.lerp(AnimationFunctions.easeIn(localT), 0, start_angle);
        }
        else if (offset == 0.5f) {
            if (be.processingTicks != -1)
                verticalAngle = Mth.lerp(AnimationFunctions.easeOut(drainProgress), start_angle, end_angle);
            else
                verticalAngle = start_angle;
        }
        else {
            float localT = (offset - 0.5f) / (1f - 0.5f);
            verticalAngle = Mth.lerp(AnimationFunctions.easeIn(localT), end_angle, 0);
        }

        verticalAngle *= insertedFrom.getAxisDirection().getStep();
        return verticalAngle;
    }

    protected void renderFluid(TundishBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, float drainProgress) {
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float yMin = 3f / 16f;
            float yOffset = (8 / 16f) * level;

            BlockState state = be.getBlockState();
            boolean alongZaxis = state.getValue(AXIS).equals(Direction.Axis.Z);
            boolean front = state.getValue(TundishBlock.FRONT);
            boolean rear = state.getValue(TundishBlock.REAR);

            // [ trapezoid faces ]
            // --- base (rectangle depending on oriented axis)
            // coordinates along Z axis
            float minSide_base = 5 / 16f;
            float minEdge_base = rear ? 0f : 2 / 16f;
            float maxSide_base = 11 / 16f;
            float maxEdge_base = front ? 1f : 14 / 16f;
            // coordinates rotation (90°)
            float xMin_base = alongZaxis ? minSide_base : minEdge_base;
            float zMin_base = alongZaxis ? minEdge_base : minSide_base;
            float xMax_base = alongZaxis ? maxSide_base : maxEdge_base;
            float zMax_base = alongZaxis ? maxEdge_base : maxSide_base;

            // --- top (square)
            // coordinates along Z axis
            float minSide_top = 2 / 16f + (1 - level) * 1 / 16f;
            float minEdge_top = rear ? 0f : 2 / 16f;
            float maxSide_top = 13 / 16f + level * 1 / 16f;
            float maxEdge_top = front ? 1f : 14 / 16f;
            // coordinates rotation (90°)
            float xMin_top = alongZaxis ? minSide_top : minEdge_top;
            float zMin_top = alongZaxis ? minEdge_top : minSide_top;
            float xMax_top = alongZaxis ? maxSide_top : maxEdge_top;
            float zMax_top = alongZaxis ? maxEdge_top : maxSide_top;

            CMFluidRenderHelper.renderTrapezoidFluidBox(fluidStack,
                    xMin_base, xMax_base, yMin, zMin_base, zMax_base,
                    xMin_top, xMax_top, yOffset, zMin_top, zMax_top,
                    buffer, ms, light, false, false);
        }

        // Fluid emptying render
        ItemStack heldItemStack = be.getHeldItemStack();
        if (heldItemStack.isEmpty())
            return;
        FluidStack fluidStack2 = GenericItemEmptying.emptyItem(be.getLevel(), heldItemStack, true)
                .getFirst();
        if (fluidStack2.isEmpty()) {
            if (fluidStack.isEmpty())
                return;
            fluidStack2 = fluidStack;
        }

        if (be.processingTicks != -1) {
            float radius = (float) (Math.pow(((2 * drainProgress) - 1), 2) - 1) / 32f;
            if (isLadle(heldItemStack) || isPackage(heldItemStack))
                radius *= 4f;
            AABB bb = new AABB(.5f, 1.1f, .5f, .5f, .25f, .5f).inflate(radius, 0, radius);

            Direction insertedFrom = be.heldItem.insertedFrom;
            bb.move(insertedFrom.getStepX(), 0, insertedFrom.getStepZ());
            CMFluidRenderHelper.renderFlowingFluidBox(fluidStack2, (float) bb.minX, (float) bb.minY, (float) bb.minZ,
                    (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, buffer, ms, light, true, false);
        }
    }
}