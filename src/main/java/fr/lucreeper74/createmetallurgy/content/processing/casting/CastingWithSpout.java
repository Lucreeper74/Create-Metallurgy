package fr.lucreeper74.createmetallurgy.content.processing.casting;

import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CastingWithSpout extends BlockSpoutingBehaviour {

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid,
                         boolean simulate) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null)
            return 0;

        IFluidHandler fluidHandler = blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP)
                .orElse(null);
        if (fluidHandler == null)
            return 0;
        if (fluidHandler.getTanks() != 1)
            return 0;

        if (!(blockEntity instanceof CastingTableBlockEntity || blockEntity instanceof CastingBasinBlockEntity))
            return 0;

        if (!fluidHandler.isFluidValid(0, availableFluid))
            return 0;

        FluidStack containedFluid = fluidHandler.getFluidInTank(0);
        if (!(containedFluid.isEmpty() || containedFluid.isFluidEqual(availableFluid)))
            return 0;

        //Do not fill if already a cast item in table
        IItemHandler itemHandler = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP)
                .orElse(null);
        if(itemHandler == null)
            return 0;

        ItemStack containedItem = itemHandler.getStackInSlot(0);
        if(!containedItem.isEmpty())
            return 0;

        // Do not fill if it would only partially fill the table (unless > 1000mb)
        int amount = availableFluid.getAmount();
        if (amount < 1000
                && fluidHandler.fill(FluidHelper.copyStackWithAmount(availableFluid, amount + 1), IFluidHandler.FluidAction.SIMULATE) > amount)
            return 0;

        // Return amount filled into the table/basin
        return fluidHandler.fill(availableFluid, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }

    public static void registerDefaults() {
        addCustomSpoutInteraction(CreateMetallurgy.genRL("spout_casting"), new CastingWithSpout());
    }
}