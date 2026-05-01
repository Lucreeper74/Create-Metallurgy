package fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoundryBasinBlockEntity extends BasinBlockEntity {

    List<IntAttached<FluidStack>> visualizedOutputFluids;

    public FoundryBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInventory = (BasinInventory) new BasinInventory(3, this).withMaxStackSize(9);
        outputInventory = new BasinInventory(4, this).forbidInsertion().withMaxStackSize(9);
        itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);

        visualizedOutputFluids = Collections.synchronizedList(new ArrayList<>());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.FOUNDRY_BASIN.get(),
                (be, context) -> be.itemCapability
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMBlockEntityTypes.FOUNDRY_BASIN.get(),
                (be, context) -> be.fluidCapability
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 4, 1000, true);
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 4, 1000, true)
                .forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper(outputTank.getCapability(), inputTank.getCapability());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        if (!clientPacket)
            return;

        NBTHelper.iterateCompoundList(compound.getList("VisualizedFluids", Tag.TAG_COMPOUND),
                c -> visualizedOutputFluids
                        .add(IntAttached.with(OUTPUT_ANIMATION_TIME, FluidStack.parseOptional(registries, c))));
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        if (!clientPacket)
            return;

        NBTHelper.iterateCompoundList(compound.getList("VisualizedFluids", Tag.TAG_COMPOUND),
                c -> visualizedOutputFluids
                        .add(IntAttached.with(OUTPUT_ANIMATION_TIME, FluidStack.parseOptional(registries, c))));
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
                    SoundSource.BLOCKS, .5f, .5f + level.getRandom().nextFloat());
        } else {
            level.setBlockAndUpdate(worldPosition, blockState.setValue(FoundryBasinBlock.FACING, clickedFace));
            level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_STEP,
                    SoundSource.BLOCKS, .5f, .5f + level.getRandom().nextFloat());
        }
    }

    public void tryEmptyingWithSpoutput() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof FoundryBasinBlock))
            return;
        Direction direction = blockState.getValue(FoundryBasinBlock.FACING);
        BlockPos output = worldPosition.below().relative(direction);
        BlockEntity be = level.getBlockEntity(output);

        DirectBeltInputBehaviour directBeltInputBehaviour =
                BlockEntityBehaviour.get(level, output, DirectBeltInputBehaviour.TYPE);
        if (directBeltInputBehaviour == null || !directBeltInputBehaviour.canInsertFromSide(direction))
            return;

        IFluidHandler targetTank = be == null ? null
                : level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), direction.getOpposite());
        IFluidHandler basinTank = getOutputTank().getCapability();

        if (targetTank == null || basinTank == null)
            return;

        FluidStack drained = basinTank.drain(basinTank.getTankCapacity(0), IFluidHandler.FluidAction.SIMULATE);

        if (drained.isEmpty())
            return;

        int filled = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained, IFluidHandler.FluidAction.SIMULATE)
                : targetTank.fill(drained, IFluidHandler.FluidAction.SIMULATE);

        if (filled > 0) {
            drained = basinTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            if (targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler)
                ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(drained, IFluidHandler.FluidAction.EXECUTE);
            else
                targetTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);

            visualizedOutputFluids.add(IntAttached.withZero(drained.copy()));
            notifyChangeOfContents();
            sendData();
        }
    }

    public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        outputInventory.allowInsertion();
        outputTank.allowInsertion();
        boolean acceptOutputsInner = acceptOutputsInner(outputItems, outputFluids, simulate);
        outputInventory.forbidInsertion();
        outputTank.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof BasinBlock))
            return false;

        IItemHandler targetInv = outputInventory;
        IFluidHandler targetTank = outputTank.getCapability();

        if (targetInv == null && !outputItems.isEmpty())
            return false;
        if (!acceptItemOutputs(outputItems, simulate, targetInv))
            return false;
        if (outputFluids.isEmpty())
            return true;
        return acceptFluidOutputs(outputFluids, simulate, targetTank);
    }

    private boolean acceptFluidOutputs(List<FluidStack> outputFluids, boolean simulate, IFluidHandler targetTank) {
        for (FluidStack fluidResult : outputFluids) {
            IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
                    ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidResult.copy(), action)
                    : targetTank.fill(fluidResult.copy(), action);
            if (fill != fluidResult.getAmount())
                return false;
        }
        return true;
    }

    private boolean acceptItemOutputs(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
        for (ItemStack outputStack : outputItems) {
            if (!ItemHandlerHelper.insertItemStacked(targetInv, outputStack.copy(), simulate)
                    .isEmpty())
                return false;
        }
        return true;
    }

    public SmartFluidTankBehaviour getOutputTank() {
        return outputTank;
    }

    // CLIENT THINGS -----------------
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CMLang.translate("gui.goggles.foundrybasin_contents")
                .forGoggles(tooltip);

        boolean isEmpty = true;

        for (int i = 0; i < itemCapability.getSlots(); i++) {
            ItemStack stackInSlot = itemCapability.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            CMLang.text("")
                    .add(Component.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CMLang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluidCapability.getTanks(); i++) {
            FluidStack fluidStack = fluidCapability.getFluidInTank(i);
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