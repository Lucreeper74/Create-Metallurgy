package fr.lucreeper74.createmetallurgy.content.belt_grinder;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

public class BeltGrinderInstance extends SingleRotatingInstance<BeltGrinderBlockEntity> {
    public BeltGrinderInstance(MaterialManager materialManager, BeltGrinderBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }
    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(shaft());
    }
}