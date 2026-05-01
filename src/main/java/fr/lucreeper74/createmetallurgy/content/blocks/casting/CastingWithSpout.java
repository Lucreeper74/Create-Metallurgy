package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.basin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.table.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public enum CastingWithSpout implements BlockSpoutingBehaviour {
    INSTANCE;

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid,
                         boolean simulate) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null)
            return 0;

        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), Direction.UP);
        if (fluidHandler == null)
            return 0;
        if (fluidHandler.getTanks() != 1)
            return 0;

        if (!(blockEntity instanceof CastingTableBlockEntity || blockEntity instanceof CastingBasinBlockEntity))
            return 0;

        if (!fluidHandler.isFluidValid(0, availableFluid))
            return 0;

        FluidStack containedFluid = fluidHandler.getFluidInTank(0);
        if (!(containedFluid.isEmpty() || FluidStack.isSameFluidSameComponents(containedFluid, availableFluid)))
            return 0;

        // Do not fill if already a cast item in table
        IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), Direction.UP);
        if (itemHandler == null)
            return 0;

        ItemStack containedItem = itemHandler.getStackInSlot(0);
        if (!containedItem.isEmpty())
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
        BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(CMBlockEntityTypes.CASTING_BASIN.get(), CastingWithSpout.INSTANCE);
        BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(CMBlockEntityTypes.CASTING_TABLE.get(), CastingWithSpout.INSTANCE);
    }
}