package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.foundation.block.connected.CTModel;
import fr.lucreeper74.createmetallurgy.registries.CMSpriteShifts;
import fr.lucreeper74.createmetallurgy.utils.CMConnectivityHandler;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock.*;
import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock.Shape.*;

public class CrucibleModel extends CTModel {

    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();

    public CrucibleModel(BakedModel originalModel) {
        super(originalModel, new CrucibleCTBehaviour(CMSpriteShifts.INDUSTRIAL_LADLE, CMSpriteShifts.INDUSTRIAL_LADLE_WINDOW, CMSpriteShifts.INDUSTRIAL_LADLE_TOP,
                CMSpriteShifts.INDUSTRIAL_LADLE_INNER));
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state,
                                                ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        for (Direction d : Iterate.horizontalDirections)
            cullData.setCulled(d, (CMConnectivityHandler.isConnected(world, pos, pos.relative(d)) && !state.getValue(TOP)) || state.getValue(SHAPE).equals(INNER));

        return builder.with(CULL_PROPERTY, cullData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        if (side != null)
            return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>();
        for (Direction d : Iterate.directions) {
            if (extraData.has(CULL_PROPERTY) && extraData.get(CULL_PROPERTY)
                    .isCulled(d))
                continue;
            quads.addAll(super.getQuads(state, d, rand, extraData, renderType));
        }
        quads.addAll(super.getQuads(state, null, rand, extraData, renderType));
        return quads;
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis()
                    .isVertical())
                return;
            culledFaces[face.get2DDataValue()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[face.get2DDataValue()];
        }
    }
}