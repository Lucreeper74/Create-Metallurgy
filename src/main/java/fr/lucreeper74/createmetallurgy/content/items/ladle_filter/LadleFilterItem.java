package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class LadleFilterItem extends Item implements MenuProvider, ItemCopyingRecipe.SupportsItemCopying {

    public LadleFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getDisplayName() {
        return getDescription();
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.PASS;
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer)
                NetworkHooks.openScreen(serverPlayer, this, buf -> {
                    buf.writeItem(heldItem);
                });
            return InteractionResultHolder.success(heldItem);
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return LadleFilterMenu.create(id, inv, heldItem);
    }
}
