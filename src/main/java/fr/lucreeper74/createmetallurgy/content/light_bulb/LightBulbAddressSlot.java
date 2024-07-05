package fr.lucreeper74.createmetallurgy.content.light_bulb;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LightBulbAddressSlot extends ValueBoxTransform {

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        Direction facing = state.getValue(LightBulbBlock.FACING);
        Vec3 location = VecHelper.voxelSpace(8, 11.5, 8);

        if (facing.getAxis()
                .isHorizontal()) {
            location = VecHelper.voxelSpace(8, 8, 11.5);
            return rotateHorizontally(state, location);
        }

        location = VecHelper.rotateCentered(location, facing == Direction.DOWN ? 180 : 0, Direction.Axis.X);
        return location;
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        Direction facing = state.getValue(LightBulbBlock.FACING);
        float yRot = facing.getAxis()
                .isVertical() ? 0 : AngleHelper.horizontalAngle(facing) + 180;
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        TransformStack.cast(ms)
                .rotateY(yRot)
                .rotateX(xRot);
    }
}
