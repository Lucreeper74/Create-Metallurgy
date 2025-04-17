package fr.lucreeper74.createmetallurgy.content.items;

import com.simibubi.create.AllSoundEvents;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FoundryUnitItem extends Item {
    public FoundryUnitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();

        if (context.getLevel().getBlockEntity(clickedPos) instanceof CrucibleBlockEntity be) {
            ItemStack item = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);
            Level level = context.getLevel();
            CrucibleBlockEntity cBE = be.getControllerBE();

            if (!cBE.foundry.isActive()) {
                cBE.updateLadleState(true);
                level.playSound(null, clickedPos, AllSoundEvents.WRENCH_ROTATE.getMainEvent(), SoundSource.PLAYERS, .2f,
                        1f + RandomSource.create().nextFloat());

                if (!context.getPlayer().isCreative())
                    item.shrink(1);

                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
