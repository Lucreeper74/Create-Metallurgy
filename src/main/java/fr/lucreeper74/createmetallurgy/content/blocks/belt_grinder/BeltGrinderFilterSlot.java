package fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeltGrinderFilterSlot extends ValueBoxTransform {

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return VecHelper.rotateCentered(VecHelper.voxelSpace(8, 12.5, 5), angleY(state), Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        TransformStack.of(ms)
                .rotateYDegrees(angleY(state))
                .rotateXDegrees(90);
    }

    protected float angleY(BlockState state) {
        return AngleHelper.horizontalAngle(state.getValue(BeltGrinderBlock.HORIZONTAL_FACING).getOpposite());
    }
}
