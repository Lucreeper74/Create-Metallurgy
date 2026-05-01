package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrucibleCTBehaviour extends ConnectedTextureBehaviour.Base {

    protected CTSpriteShiftEntry topShift;
    protected CTSpriteShiftEntry layerShift;
    protected CTSpriteShiftEntry innerShift;
    protected CTSpriteShiftEntry windowShift;

    public CrucibleCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry windowShift, CTSpriteShiftEntry topShift, CTSpriteShiftEntry innerShift) {
        this.layerShift = layerShift;
        this.topShift = topShift;
        this.windowShift = windowShift;
        this.innerShift = innerShift;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (sprite != null && direction.getAxis() == Direction.Axis.Y && innerShift.getOriginal() == sprite)
            return innerShift;

        if (state.getValue(CrucibleBlock.WINDOW) && direction.getAxis().isHorizontal())
            return windowShift;

        return direction.getAxis()
                .isHorizontal() ? layerShift : topShift;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
                              Direction face) {
        boolean blockMatch = state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);

        if (face.getAxis().isHorizontal())
            return blockMatch && ((state.getValue(CrucibleBlock.WINDOW) == other.getValue(CrucibleBlock.WINDOW))
                    || ((!state.getValue(CrucibleBlock.WINDOW) && other.getValue(CrucibleBlock.WINDOW)) && (pos.relative(Direction.UP).equals(otherPos)
                    || pos.relative(Direction.DOWN).equals(otherPos))));
        else
            return blockMatch;
    }
}
