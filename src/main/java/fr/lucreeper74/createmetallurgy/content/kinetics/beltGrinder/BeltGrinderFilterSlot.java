package fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeltGrinderFilterSlot extends ValueBoxTransform {

    @Override
    public Vec3 getLocalOffset(BlockState state) {
        int offset = -3;
        Vec3 x = VecHelper.voxelSpace(8, 12.5f, 8 + offset);
        Vec3 z = VecHelper.voxelSpace(8 + offset, 12.5f, 8);
        return z;
    }

    @Override
    public void rotate(BlockState state, PoseStack ms) {
        TransformStack.cast(ms)
                .rotateX(90);
    }

}
