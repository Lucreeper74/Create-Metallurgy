package fr.lucreeper74.createmetallurgy.content.foundry_lids.lid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static fr.lucreeper74.createmetallurgy.content.foundry_lids.lid.FoundryLidBlock.ON_FOUNDRY_BASIN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryLidRenderer extends SafeBlockEntityRenderer<FoundryLidBlockEntity> {

    public FoundryLidRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(FoundryLidBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.getBlockState().getValue(ON_FOUNDRY_BASIN))
            return;

        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(HORIZONTAL_FACING);
        float dialPivot = 5.75f / 16;

        CachedBufferer.partial(CMPartialModels.THERMOMETER_GAUGE, blockState)
                .centre()
                .rotateY(-facing.toYRot())
                .unCentre()
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

        CachedBufferer.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState)
                .centre()
                .rotateY(-facing.toYRot())
                .unCentre()
                .translate(0, dialPivot, dialPivot)
                .rotateX(be.gauge.getValue(partialTicks) * -90)
                .translate(0, -dialPivot, -dialPivot)
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

    }
}
