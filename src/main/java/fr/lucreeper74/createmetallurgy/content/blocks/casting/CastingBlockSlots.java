package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CastingBlockSlots {

    public static class LockSlot extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(3, 11, 15.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis()
                    .isHorizontal();
        }

        @Override
        public int getOverrideColor() {
            return 0xFFFFFF;
        }
    }

    public static class FilterSlot extends ValueBoxTransform.Sided {

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(13, 11, 15.5);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal();
        }
    }
}