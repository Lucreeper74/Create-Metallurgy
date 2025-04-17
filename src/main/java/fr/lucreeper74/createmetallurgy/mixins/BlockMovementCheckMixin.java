package fr.lucreeper74.createmetallurgy.mixins;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockMovementChecks.class, remap = false)
public class BlockMovementCheckMixin {

    @Inject(method = "isBlockAttachedTowardsFallback", at = @At("HEAD"), cancellable = true)
    private static void isBlockAttachedTowardsFallback(BlockState state, Level world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        if (state.getBlock() instanceof CrucibleBlock) {
            info.setReturnValue(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
        }
    }
}
