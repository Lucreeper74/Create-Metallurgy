package fr.lucreeper74.createmetallurgy.content.foundry_basin;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.*;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoundryBasinBlockEntity extends BasinBlockEntity {

    List<IntAttached<FluidStack>> visualizedOutputFluids;

    public FoundryBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInventory = (BasinInventory) new BasinInventory(1, this).withMaxStackSize(9);
        outputInventory = new BasinInventory(1, this).forbidInsertion().withMaxStackSize(9);

        visualizedOutputFluids = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        if (!clientPacket)
            return;
        NBTHelper.iterateCompoundList(compound.getList("VisualizedFluids", Tag.TAG_COMPOUND),
                c -> visualizedOutputFluids
                        .add(IntAttached.with(OUTPUT_ANIMATION_TIME, FluidStack.loadFluidStackFromNBT(c))));
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        if (!clientPacket)
            return;
        compound.put("VisualizedFluids", NBTHelper.writeCompoundList(visualizedOutputFluids, ia -> ia.getValue()
                .writeToNBT(new CompoundTag())));
        visualizedOutputFluids.clear();
    }

    @Override
    public void lazyTick() {
        if (!level.isClientSide) {
            if (isEmpty())
                return;
            notifyChangeOfContents();
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(worldPosition.above(2));
        if (!(blockEntity instanceof MechanicalMixerBlockEntity)) {
            setAreFluidsMoving(false);
            return;
        }

        MechanicalMixerBlockEntity mixer = (MechanicalMixerBlockEntity) blockEntity;
        setAreFluidsMoving(mixer.running && mixer.runningTicks <= 20);
    }

    @Override
    public void tick() {
        if (!level.isClientSide)
            if (!outputTank.isEmpty() && getBlockState().getValue(FoundryBasinBlock.FACING) != Direction.DOWN)
                tryEmptyingWithSpoutput();

        super.tick();
    }

    @Override
    public void onWrenched(Direction clickedFace) {
        if (clickedFace.getAxis().isVertical())
            return;
        BlockState blockState = getBlockState();
        Direction facing = blockState.getValue(FoundryBasinBlock.FACING);
        if (facing == clickedFace) {
            level.setBlockAndUpdate(worldPosition, blockState.setValue(FoundryBasinBlock.FACING, Direction.DOWN));
            level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_HIT,
                    SoundSource.BLOCKS, .5f, .5f + Create.RANDOM.nextFloat());
        } else {
            level.setBlockAndUpdate(worldPosition, blockState.setValue(FoundryBasinBlock.FACING, clickedFace));
            level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_STEP,
                    SoundSource.BLOCKS, .5f, .5f + Create.RANDOM.nextFloat());
        }
    }

    public void tryEmptyingWithSpoutput() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof FoundryBasinBlock))
            return;
        Direction direction = blockState.getValue(FoundryBasinBlock.FACING);
        BlockEntity be = level.getBlockEntity(worldPosition.below()
                .relative(direction));

        IFluidHandler targetTank = be == null ? null
                : be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                .orElse(null);

        if (targetTank == null)
            return;

        FluidStack fluidInTank = outputTank.getPrimaryHandler().getFluid();

        for (boolean simulate : Iterate.trueAndFalse) {
            IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidInTank.copy(), action)
                    : targetTank.fill(fluidInTank.copy(), action);

            if (fill <= 0)
                break;
            if (simulate)
                continue;

            visualizedOutputFluids.add(IntAttached.withZero(fluidInTank.copy()));
            fluidInTank.shrink(fill);
            notifyChangeOfContents();
            sendData();
        }
    }

    public SmartFluidTankBehaviour getOutputTank() {
        return outputTank;
    }

    // CLIENT THINGS -----------------
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CMLang.translate("gui.goggles.foundrybasin_contents")
                .forGoggles(tooltip);

        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());
        IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
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
        for (int i = 0; i < fluids.getTanks(); i++) {
            FluidStack fluidStack = fluids.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;
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