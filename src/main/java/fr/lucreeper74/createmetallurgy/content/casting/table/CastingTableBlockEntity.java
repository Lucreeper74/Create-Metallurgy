package fr.lucreeper74.createmetallurgy.content.casting.table;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class CastingTableBlockEntity extends CastingBlockEntity implements IHaveGoggleInformation {

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
        return r.getType() == CMRecipeTypes.CASTING_IN_TABLE.getType();
    }

    private static final Object CastingInTableRecipesKey = new Object();

    protected Object getRecipeCacheKey() {
        return CastingInTableRecipesKey;
    }


    // CLIENT THINGS -----------------
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CMLang.translate("gui.goggles.castingtable_contents")
                .forGoggles(tooltip);

        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());
        IFluidHandler fluids = getFluidTank();
        boolean isEmpty = true;

        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            CMLang.text("")
                    .add(Components.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CMLang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = CMLang.translate("generic.unit.millibuckets");
        FluidStack fluidStack = fluids.getFluidInTank(0);
        if (!fluidStack.isEmpty()) {
            CMLang.text("")
                    .add(CMLang.fluidName(fluidStack)
                            .add(CMLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CMLang.number(fluidStack.getAmount())
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