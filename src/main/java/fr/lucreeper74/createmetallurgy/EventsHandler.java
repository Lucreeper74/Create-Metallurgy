package fr.lucreeper74.createmetallurgy;

import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "createmetallurgy")
public class EventsHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (event.getEntity() == null)
            return;
        List<Component> tooltip = event.getToolTip();
        Item item = event.getItemStack().getItem();

        if(ForgeRegistries.ITEMS.getKey(item).getNamespace() != "createmetallurgy")
            return;
        String path = "createmetallurgy." + ForgeRegistries.ITEMS.getKey(item).getPath();
        List<Component> tooltipList = new ArrayList<>();
        if(!Component.translatable(path + ".tooltip.summary").getString().equals(path + ".tooltip.summary")) {
            if (Screen.hasShiftDown()) {
                tooltipList.add(Lang.translateDirect("tooltip.holdForDescription", Component.translatable("create.tooltip.keyShift").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.DARK_GRAY));
                tooltipList.add(Components.immutableEmpty());
                tooltipList.addAll(TooltipHelper.cutStringTextComponent(Component.translatable(path + ".tooltip.summary").getString(), TooltipHelper.Palette.STANDARD_CREATE));

                if(!Component.translatable(path + ".tooltip.condition1").getString().equals(path + ".tooltip.condition1")) {
                    tooltipList.add(Components.immutableEmpty());
                    tooltipList.add(Component.translatable(path + ".tooltip.condition1").withStyle(ChatFormatting.GRAY));
                    tooltipList.addAll(TooltipHelper.cutStringTextComponent(Component.translatable(path + ".tooltip.behaviour1").getString(), TooltipHelper.Palette.STANDARD_CREATE.primary(), TooltipHelper.Palette.STANDARD_CREATE.highlight(), 1));
                    if(!Component.translatable(path + ".tooltip.condition2").getString().equals(path + ".tooltip.condition2")) {
                        tooltipList.add(Component.translatable(path + ".tooltip.condition2").withStyle(ChatFormatting.GRAY));
                        tooltipList.addAll(TooltipHelper.cutStringTextComponent(Component.translatable(path + ".tooltip.behaviour2").getString(), TooltipHelper.Palette.STANDARD_CREATE.primary(), TooltipHelper.Palette.STANDARD_CREATE.highlight(), 1));
                    }
                }
            } else {
                tooltipList.add(Lang.translateDirect("tooltip.holdForDescription", Component.translatable("create.tooltip.keyShift").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        tooltip.addAll(1,tooltipList);
    }
}
