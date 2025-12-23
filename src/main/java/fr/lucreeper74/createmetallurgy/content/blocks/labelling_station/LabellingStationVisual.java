package fr.lucreeper74.createmetallurgy.content.blocks.labelling_station;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LabellingStationVisual<T extends LabellingStationBlockEntity> extends AbstractBlockEntityVisual<T> implements SimpleDynamicVisual {
    public LabellingStationVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

    }

    @Override
    public void updateLight(float partialTick) {

    }

    @Override
    protected void _delete() {

    }

    @Override
    public void beginFrame(Context ctx) {

    }
}