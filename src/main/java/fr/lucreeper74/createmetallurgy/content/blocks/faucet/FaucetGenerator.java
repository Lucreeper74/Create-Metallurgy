package fr.lucreeper74.createmetallurgy.content.blocks.faucet;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class FaucetGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return (int) state.getValue(FaucetBlock.FACING).toYRot() + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String variant = "";
        if (state.getValue(FaucetBlock.FACING).equals(Direction.DOWN))
            variant += "_down";

        if (state.getValue(FaucetBlock.OPEN))
            variant += "_open";

        return prov.models().getExistingFile(prov.modLoc("block/faucet/block" + variant));
    }
}
