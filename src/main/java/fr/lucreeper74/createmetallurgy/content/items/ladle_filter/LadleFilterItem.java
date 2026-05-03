package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.registries.CMDataComponents;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LadleFilterItem extends FilterItem {

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
    public List<Component> makeSummary(ItemStack filter) {
        List<Component> list = new ArrayList<>();

        String address = LadleItem.getAddress(filter);
        list.add(CMLang.text("-> ")
                .style(ChatFormatting.GRAY)
                .add((address.isBlank() ? CMLang.translateDirect("gui.ladle_filter.blank_address") :
                        address.equalsIgnoreCase("*") ? CMLang.translateDirect("gui.ladle_filter.any_address") : CMLang.text(address).component())
                        .withStyle(ChatFormatting.GOLD))
                .component());

        Fluid fluidFilter = filter.getOrDefault(CMDataComponents.LADLE_FILTER_FLUID, Fluids.EMPTY);
        LangBuilder fluidLang = fluidFilter.equals(Fluids.EMPTY) ? CMLang.translate("gui.ladle_filter.any_fluid") : CMLang.fluidName(fluidFilter);

        int filledAmount = filter.getOrDefault(CMDataComponents.LADLE_FILTER_FLUID_AMOUNT, -1);
        int comparator = filter.getOrDefault(CMDataComponents.LADLE_FILTER_COMPARATOR, 0);
        MutableComponent amountLang = fluidLang
                .space()
                .add(LadleFilterScreen.COMPARATORS_LANG_LIST.get(comparator))
                .space()
                .add(CMLang.text(String.valueOf(filledAmount)))
                .add(CreateLang.translateDirect("generic.unit.millibuckets"))
                .component();

        if (filledAmount >= 0)
            list.add((filledAmount == 0 && comparator != 1 && comparator != 2 ? CMLang.translateDirect("gui.ladle_filter.empty_fluid") : amountLang)
                    .withStyle(ChatFormatting.WHITE));

        return list;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer)
                player.openMenu(this, buf -> {
                    ItemStack.STREAM_CODEC.encode(buf, heldItem);
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

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.PACKAGE_ADDRESS;
    }

    @Override
    public FilterItemStack makeStackWrapper(ItemStack filter) {
        return new LadleFilterItemStack(filter);
    }

    @Override
    public ItemStack[] getFilterItems(ItemStack stack) {
        return new ItemStack[0];
    }
}
