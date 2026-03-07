package fr.lucreeper74.createmetallurgy.content.blocks.labeling_station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LabelingStationRenderer extends SmartBlockEntityRenderer<LabelingStationBlockEntity> {

    public LabelingStationRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LabelingStationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        ItemStack renderedBox = be.getRenderedBox();
        float ladleOffset = be.getLadleOffset(partialTicks);
        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(LabelingStationBlock.FACING);

        SuperByteBuffer sbb = CachedBuffers.partial(CMPartialModels.LABELLING_STATION_TRAY_REGULAR, blockState);
        sbb.translate(Vec3.atLowerCornerOf(Direction.UP.getNormal())
                        .scale(-ladleOffset*.5f))
                .rotateYCenteredDegrees(facing.toYRot())
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        if (!renderedBox.isEmpty()) {
            ms.pushPose();
            var msr = TransformStack.of(ms);
//            msr.translate(Vec3.atLowerCornerOf(facing.getNormal())
//                            .scale(ladleOffset))
                    msr.translate(.5f, .5f, .5f)
                    .rotateYDegrees(facing.toYRot())
                    .translate(0, 2 / 16f, 0)
                    .scale(1.49f, 1.49f, 1.49f);
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(null, renderedBox, ItemDisplayContext.FIXED, false, ms, buffer, be.getLevel(), light,
                            overlay, 0);
            ms.popPose();
        }
    }
}