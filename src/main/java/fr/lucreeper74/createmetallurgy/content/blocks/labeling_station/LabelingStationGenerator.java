package fr.lucreeper74.createmetallurgy.content.blocks.labeling_station;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class LabelingStationGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(LabelingStationBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        String suffix = state.getOptionalValue(LabelingStationBlock.LINKED)
                .orElse(false) ? "linked" : state.getValue(LabelingStationBlock.POWERED) ? "powered" : "";
        return state.getValue(LabelingStationBlock.FACING)
                .getAxis() == Direction.Axis.Y ? AssetLookup.partialBaseModel(ctx, prov, "vertical", suffix)
                : AssetLookup.partialBaseModel(ctx, prov, suffix);
    }
}