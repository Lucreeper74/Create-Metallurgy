package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

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
        boolean window = state.getValue(CrucibleBlock.WINDOW);
        CrucibleBlock.Shape shape = state.getValue(CrucibleBlock.SHAPE);

        String modelName = getString(top, bottom, shape);

        if (window)
            return prov.models().withExistingParent("block/industrial_crucible/block_" + modelName + "_window", prov.modLoc("block/industrial_crucible/block_" + modelName))
                    .texture("1", prov.modLoc("block/industrial_crucible/crucible_window"));

        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }

    private static @NotNull String getString(Boolean top, Boolean bottom, CrucibleBlock.Shape shape) {
        String shapeName = "middle";
        if (top && bottom && !shape.equals(CrucibleBlock.Shape.INNER))
            shapeName = "single";
        else if (top && !shape.equals(CrucibleBlock.Shape.INNER))
            shapeName = "top";
        else if (bottom)
            shapeName = "bottom";

        return shapeName + (top && (shape.isWall() || shape.isCorner()) ? "_" + shape.getSerializedName() : "");
    }
}