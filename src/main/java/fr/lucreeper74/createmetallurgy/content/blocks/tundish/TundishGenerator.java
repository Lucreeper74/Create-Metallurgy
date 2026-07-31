package fr.lucreeper74.createmetallurgy.content.blocks.tundish;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class TundishGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return state.getValue(TundishBlock.ALONG_Z_AXIS) ? 0 : -90;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {

        int mask = (state.getValue(TundishBlock.FRONT) ? 1 : 0) | (state.getValue(TundishBlock.REAR) ? 2 : 0);
        String modelName = switch (mask) {
            case 1 -> "start";
            case 2 -> "end";
            case 3 -> "middle";
            default -> "single";
        };
        ResourceLocation location = prov.modLoc("block/tundish/" + modelName);
        return prov.models()
                .getExistingFile(location);
    }
}