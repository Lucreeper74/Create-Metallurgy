package fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

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
        Boolean window = state.getValue(FoundryLidBlock.WINDOW);
        Boolean open = state.getValue(FoundryLidBlock.OPEN);

        String variant = "";
        if (open)
            variant += "_open";
        if (window)
            variant += "_window";

        return prov.models().getExistingFile(prov.modLoc("block/foundry_lid/block" + variant));
    }
}
