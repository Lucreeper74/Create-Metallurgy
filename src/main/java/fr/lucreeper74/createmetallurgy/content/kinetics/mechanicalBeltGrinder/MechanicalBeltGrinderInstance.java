package fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalBeltGrinder;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

public class MechanicalBeltGrinderInstance extends SingleRotatingInstance<MechanicalBeltGrinderBlockEntity> {
    public MechanicalBeltGrinderInstance(MaterialManager materialManager, MechanicalBeltGrinderBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }
    @Override
    protected Instancer<RotatingData> getModel() {
            return getRotatingMaterial().getModel(shaft());
    }
}
