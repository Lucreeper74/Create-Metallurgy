package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import com.simibubi.create.AllKeys;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import joptsimple.internal.Strings;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FoundryData {

    private final CrucibleBlockEntity be;
    protected FoundryItemHandler inputInv;

    // Heat management
    private int currentHeat;
    public boolean needsHeatLevelUpdate;

    // For rendering purposes only
    public LerpedFloat gauge = LerpedFloat.linear();

    public FoundryData(CrucibleBlockEntity be) {
        this.be = be;
        this.inputInv = new FoundryItemHandler();
    }

    public void tick() {
        inputInv.tick();

        if (be.getLevel() == null)
            return;

        if (be.getLevel().isClientSide) {
            gauge.tickChaser();
            return;
        }

        if (needsHeatLevelUpdate && updateTemperature()) {
            be.notifyUpdate();
            inputInv.notifyChangeOfContent();
        }
    }

    public boolean updateTemperature() {
        BlockPos controllerPos = be.getBlockPos();
        Level level = be.getLevel();
        needsHeatLevelUpdate = false;

        int prevActive = currentHeat;
        currentHeat = 0;

        for (int xOffset = 0; xOffset < be.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < be.getWidth(); zOffset++) {
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeater.findHeat(level, pos, blockState);
                currentHeat += heat;
            }
        }
        return prevActive != currentHeat;
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("CurrentHeat", currentHeat);
        nbt.putBoolean("HeatUpdate", needsHeatLevelUpdate);
        return nbt;
    }

    public void read(CompoundTag nbt, int base_width) {
        currentHeat = nbt.getInt("CurrentHeat");
        needsHeatLevelUpdate = nbt.getBoolean("HeatUpdate");
        gauge.chase(getHeatGaugeValue(base_width), .125f, LerpedFloat.Chaser.EXP);
    }

    public int getCurrentHeat() {
        return currentHeat;
    }

    public FoundryItemHandler getInputInv() {
        return inputInv;
    }

    public float getHeatGaugeValue(int base_width) {
        int base_surface = base_width * base_width;
        return (float) (getCurrentHeat() + base_surface) / ((base_surface * 2) + base_surface);
    }

    public void addToGoggleTooltip(List<Component> tooltip, boolean gaugeActive, int foundrySize) {
        if (gaugeActive) {
            CMLang.translate("foundry.status")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CMLang.builder().add(getHeatLevelComponent(foundrySize)).forGoggles(tooltip);

            tooltip.add(Component.empty());
        }

        if (AllKeys.shiftDown()) {
            int displayed = 0;

            for (int i = 0; i < inputInv.getSlots(); i++) {
                FoundryItemSlot slot = inputInv.getSlot(i);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot.isEmpty())
                    continue;

                if (displayed > 4) {
                    CMLang.text("...").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
                    break;
                }

                int duration = slot.processDuration;

                CMLang.text("")
                        .add(CreateLang.itemName(stackInSlot).style(ChatFormatting.GRAY))
                        .space()
                        .add(slot.processDuration >= 0 ?
                                progressBarComponent(duration, slot.processingTime, 9) :
                                CMLang.text("X").style(ChatFormatting.RED).style(ChatFormatting.BOLD).component())
                        .forGoggles(tooltip, 1);

                displayed++;
            }
            if (displayed > 0)
                tooltip.add(Component.empty());
        }
    }

    @NotNull
    public MutableComponent getHeatLevelComponent(int foundrySize) {
        FoundryHeatLevel heatLevel = FoundryHeatLevel.getHeatLevel(getCurrentHeat(), foundrySize);
        LangBuilder builder = CMLang.text(TooltipHelper.makeProgressBar(3, heatLevel.ordinal()));


        builder.translate("foundry." + CMLang.asId(heatLevel.name()))
                .space()
                .text("(")
                .add(CMLang.number(currentHeat))
                .space()
                .translate("generic.unit.thermal")
                .text(")")
                .space();

        builder.color(heatLevel.getTextColor());

        return builder.component();
    }

    private MutableComponent progressBarComponent(int maxValue, int progress, int maxLength) {
        int level = maxLength - ((progress * maxLength) / maxValue);

        return Component.empty()
                .append(bars(Math.min(level, 3), ChatFormatting.DARK_RED))
                .append(bars(level > 3 ? Math.min(level - 3, 3) : 0, ChatFormatting.GOLD))
                .append(bars(level > 6 ? Math.min(level - 6, 3) : 0, ChatFormatting.YELLOW))
                .append(bars(Math.max(0, maxLength - level), ChatFormatting.DARK_GRAY));
    }

    private MutableComponent bars(int count, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', count))
                .withStyle(format);
    }

    public enum FoundryHeatLevel {
        COOLING(ChatFormatting.GREEN.getColor()),
        STABLE(ChatFormatting.RED.getColor()),
        HEATING(0xE88300),
        OVERHEATING(0x5C93E8);

        private final int textColor;

        FoundryHeatLevel(int textColor) {
            this.textColor = textColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public static FoundryHeatLevel getHeatLevel(int heat, int foundrySize) {
            if (heat >= (foundrySize * 2))
                return OVERHEATING;
            if (heat > 0)
                return HEATING;
            if (heat < 0)
                return COOLING;
            return STABLE;
        }
    }
}
