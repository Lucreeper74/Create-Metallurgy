package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;

public class CastingBlockMovementBehavior implements MovementBehaviour {

    public Map<String, ItemStackHandler> getOrReadInventory(MovementContext context) {
        Map<String, ItemStackHandler> map = new HashMap<>();
        map.put("inv", new ItemStackHandler(9));
        map.forEach((s, h) -> h.deserializeNBT(context.blockEntityData.getCompound(s)));
        return map;
    }

    @Override
    public void tick(MovementContext context) {
        MovementBehaviour.super.tick(context);
        if (context.temporaryData == null || (boolean) context.temporaryData) {
            Vec3 facingVec = context.rotation.apply(Vec3.atLowerCornerOf(Direction.UP.getNormal()));
            facingVec.normalize();
            if (Direction.getNearest(facingVec.x, facingVec.y, facingVec.z) == Direction.DOWN)
                dump(context, facingVec);
        }

        if (context.world.isClientSide) {
            BlockEntity be = context.contraption.presentBlockEntities.get(context.localPos);
            if (be instanceof CastingBlockEntity castingBE) {
                castingBE.inputTank.getFluidLevel().tickChaser();
            }
        }
    }

    private void dump(MovementContext context, Vec3 facingVec) {
        getOrReadInventory(context).forEach((key, itemStackHandler) -> {
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (itemStackHandler.getStackInSlot(i)
                        .isEmpty())
                    continue;

                ItemEntity itemEntity = new ItemEntity(context.world, context.position.x, context.position.y - .45f,
                        context.position.z, itemStackHandler.getStackInSlot(i));

                itemEntity.setDeltaMovement(facingVec.scale(.05));
                context.world.addFreshEntity(itemEntity);
                itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
            context.blockEntityData.put(key, itemStackHandler.serializeNBT());
        });
        BlockEntity blockEntity = context.contraption.presentBlockEntities.get(context.localPos);
        if (blockEntity instanceof CastingBlockEntity castingBE)
            castingBE.readOnlyItems(context.blockEntityData);
        context.temporaryData = false; // did already dump, so can't any more
    }
}