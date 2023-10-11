package fr.lucreeper74.createmetallurgy.content.processing.foundrybasin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import fr.lucreeper74.createmetallurgy.utils.LANG;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class FoundryBasinBlockEntity extends BasinBlockEntity {

    public FoundryBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInventory = (BasinInventory) new BasinInventory(1, this).withMaxStackSize(10);
        outputInventory = new BasinInventory(1, this).forbidInsertion().withMaxStackSize(10);
    }

    // CLIENT THINGS -----------------
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LANG.translate("gui.goggles.foundrybasin_contents")
                .forGoggles(tooltip);

        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());
        IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
        boolean isEmpty = true;

        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            LANG.text("")
                    .add(Components.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(LANG.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = LANG.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluids.getTanks(); i++) {
            FluidStack fluidStack = fluids.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;
            LANG.text("")
                    .add(LANG.fluidName(fluidStack)
                            .add(LANG.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(LANG.number(fluidStack.getAmount())
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.remove(0);

        return true;
    }
}
