package fr.lucreeper74.createmetallurgy.content.processing.foundrylid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import fr.lucreeper74.createmetallurgy.registries.AllPModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import java.util.HashMap;

import static fr.lucreeper74.createmetallurgy.content.processing.foundrylid.FoundryLidBlock.ON_FOUNDRY_BASIN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryLidRenderer extends SafeBlockEntityRenderer<FoundryLidBlockEntity> {

    public LerpedFloat progress = LerpedFloat.linear();

    public FoundryLidRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(FoundryLidBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.getBlockState().getValue(ON_FOUNDRY_BASIN))
            return;

        Direction facing = be.getBlockState().getValue(HORIZONTAL_FACING);
        float dialPivot = 5.75f / 16;


        BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(be.getLevel()
                .getBlockState(be.getBlockPos()
                        .below(2)));
        progress.tickChaser();

        HashMap<BlazeBurnerBlock.HeatLevel, Integer> temp = new HashMap<>();

        temp.put(BlazeBurnerBlock.HeatLevel.NONE, 0);
        temp.put(BlazeBurnerBlock.HeatLevel.SMOULDERING, 500);
        temp.put(BlazeBurnerBlock.HeatLevel.KINDLED, 1000);
        temp.put(BlazeBurnerBlock.HeatLevel.SEETHING, 2000);

        progress.chase((double) temp.get(heat) / 2000, .02f, LerpedFloat.Chaser.EXP);


        CachedBufferer.partial(AllPModels.THERMOMETER_GAUGE, be.getBlockState())
                .centre()
                .rotateY(-facing.toYRot())
                .unCentre()
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

        CachedBufferer.partial(AllPartialModels.BOILER_GAUGE_DIAL, be.getBlockState())
                .centre()
                .rotateY(-facing.toYRot())
                .unCentre()
                .translate(0, dialPivot, dialPivot)
                .rotateX(progress.getValue(partialTicks) * -90)
                .translate(0, -dialPivot, -dialPivot)
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

    }
}
