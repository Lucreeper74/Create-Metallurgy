package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.AllKeys;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.MeltingInventory;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.MeltingSlot;
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

    protected MeltingInventory inputInv;

    private boolean active;
    private int currentHeat;

    // For rendering purposes only
    public LerpedFloat gauge = LerpedFloat.linear();

    protected void createInventory(CrucibleBlockEntity be, int size) {
        inputInv = new MeltingInventory(be, size);
    }

    public void tick(CrucibleBlockEntity be) {
        if (!active)
            return;
        if (be.getLevel().isClientSide) {
            gauge.tickChaser();
            gauge.chase((float) getCurrentHeat() / getMaxHeat(be), .05f, LerpedFloat.Chaser.EXP);
            return;
        }

        if (updateTemperature(be))
            be.notifyUpdate();

        // Melting Recipes
        for (int slot = 0; slot < be.getTotalSize(); slot++) {
            MeltingSlot meltingSlot = inputInv.getSlot(slot);
            if (meltingSlot.getStack().isEmpty())
                continue;

            if (meltingSlot.canMelt())
                meltingSlot.heatItem();
            else
                meltingSlot.coolItem();
        }

        be.tankInventory.process();
    }

    public boolean updateTemperature(CrucibleBlockEntity be) {
        BlockPos controllerPos = be.getBlockPos();
        Level level = be.getLevel();

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
        nbt.putBoolean("Controlled", active);
        nbt.putInt("currentHeat", currentHeat);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        active = nbt.getBoolean("Controlled");
        currentHeat = nbt.getInt("currentHeat");
    }

    public boolean isActive() {
        return active;
    }

    public int getCurrentHeat() {
        return currentHeat;
    }

    public int getMaxHeat(CrucibleBlockEntity be) {
        return be.getWidth() * be.getWidth() * 2;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MeltingInventory getInventory() {
        return inputInv;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, int foundrySize) {
        if (!isActive())
            return false;

        CMLang.translate("foundry.status")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CMLang.builder().add(getHeatLevelComponent(foundrySize)).forGoggles(tooltip);

        tooltip.add(Component.empty());

        if (AllKeys.shiftDown()) {
            int displayed = 0;

            for (int i = 0; i < inputInv.getSlots(); i++) {
                MeltingSlot slot = inputInv.getSlot(i);
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
                        .add(duration > 0 ?
                                progressBarComponent(duration, slot.processingTime, 9) :
                                CMLang.text("X").style(ChatFormatting.RED).style(ChatFormatting.BOLD).component())
                        .forGoggles(tooltip, 1);

                displayed++;
            }
            if (displayed > 0)
                tooltip.add(Component.empty());
        }

        return true;
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
