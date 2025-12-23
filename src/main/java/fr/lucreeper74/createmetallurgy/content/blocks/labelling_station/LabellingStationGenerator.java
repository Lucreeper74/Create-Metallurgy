package fr.lucreeper74.createmetallurgy.content.blocks.labelling_station;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class LabellingStationGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(LabellingStationBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        String suffix = state.getOptionalValue(LabellingStationBlock.LINKED)
                .orElse(false) ? "linked" : state.getValue(LabellingStationBlock.POWERED) ? "powered" : "";
        return state.getValue(LabellingStationBlock.FACING)
                .getAxis() == Direction.Axis.Y ? AssetLookup.partialBaseModel(ctx, prov, "vertical", suffix)
                : AssetLookup.partialBaseModel(ctx, prov, suffix);
    }
}