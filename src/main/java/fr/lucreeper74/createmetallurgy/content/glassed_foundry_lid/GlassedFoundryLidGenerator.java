package fr.lucreeper74.createmetallurgy.content.glassed_foundry_lid;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class GlassedFoundryLidGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(GlassedFoundryLidBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {

        if (state.getValue(GlassedFoundryLidBlock.UNDER_FOUNDRY_MIXER)) {
            if (state.getValue(GlassedFoundryLidBlock.OPEN))
                return AssetLookup.partialBaseModel(ctx, prov, "under_mixer_open");
            return AssetLookup.partialBaseModel(ctx, prov, "under_mixer");
        }
        if(state.getValue(GlassedFoundryLidBlock.OPEN))
            return AssetLookup.partialBaseModel(ctx, prov, "open");

        return AssetLookup.partialBaseModel(ctx, prov);
    }

}
