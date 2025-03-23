package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class CrucibleGenerator extends SpecialBlockStateGen {
    public CrucibleGenerator() {
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }


    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        Boolean top = state.getValue(CrucibleBlock.TOP);
        Boolean bottom = state.getValue(CrucibleBlock.BOTTOM);
        Boolean window = state.getValue(CrucibleBlock.WINDOW);
        CrucibleBlock.Shape shape = state.getValue(CrucibleBlock.SHAPE);

        String shapeName = "middle";
        if (top && bottom && !shape.equals(CrucibleBlock.Shape.INNER))
            shapeName = "single";
        else if (top && !shape.equals(CrucibleBlock.Shape.INNER))
            shapeName = "top";
        else if (bottom)
            shapeName = "bottom";

        String modelName = shapeName + (top && (shape.isWall() || shape.isCorner()) ? "_" + shape.getSerializedName() : ""); // (shape.isWall() || shape.equals(LadleBlock.Shape.PLAIN) ? "" : "_" + shape.getSerializedName());

        if (window)
            return prov.models().withExistingParent("block/industrial_crucible/block_" + modelName + "_window", prov.modLoc("block/industrial_crucible/block_" + modelName))
                    .texture("1", prov.modLoc("block/industrial_crucible/crucible_window"));

        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }
}