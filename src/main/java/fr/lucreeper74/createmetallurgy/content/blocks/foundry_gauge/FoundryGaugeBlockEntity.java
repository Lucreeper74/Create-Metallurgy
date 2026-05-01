package fr.lucreeper74.createmetallurgy.content.blocks.foundry_gauge;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.List;

public class FoundryGaugeBlockEntity extends FactoryPanelBlockEntity {

    public FoundryGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        panels = new EnumMap<>(FactoryPanelBlock.PanelSlot.class);
        redraw = true;
        for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            FoundryGaugeBehaviour e = new FoundryGaugeBehaviour(this, slot);
            panels.put(slot, e);
            behaviours.add(e);
        }
    }
}
