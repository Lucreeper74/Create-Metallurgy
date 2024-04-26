package fr.lucreeper74.createmetallurgy.content.processing.foundrylid;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class FoundryLidGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(FoundryLidBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {

        if (state.getValue(FoundryLidBlock.ON_FOUNDRY_BASIN)) {
            if (state.getValue(FoundryLidBlock.OPEN))
                return AssetLookup.partialBaseModel(ctx, prov, "on_a_basin_open");
            return AssetLookup.partialBaseModel(ctx, prov, "on_a_basin");
        }
        if(state.getValue(FoundryLidBlock.OPEN))
            return AssetLookup.partialBaseModel(ctx, prov, "open");

        return AssetLookup.partialBaseModel(ctx, prov);
    }

}
