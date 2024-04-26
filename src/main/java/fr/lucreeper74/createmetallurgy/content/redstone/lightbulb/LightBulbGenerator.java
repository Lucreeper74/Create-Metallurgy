package fr.lucreeper74.createmetallurgy.content.redstone.lightbulb;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class LightBulbGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(LightBulbBlock.FACING);
        return facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 270;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction facing = state.getValue(LightBulbBlock.FACING);
        return facing.getAxis()
                .isVertical() ? 180 : horizontalAngle(facing);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        String level = state.getValue(LightBulbBlock.LEVEL).toString();
        return AssetLookup.partialBaseModel(ctx, prov, level);
    }
}
