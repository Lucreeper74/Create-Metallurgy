package fr.lucreeper74.createmetallurgy.content.blocks.labelling_station;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class LabellingStationBlockEntity extends SmartBlockEntity {

    public boolean redstonePowered;
    public int buttonCooldown;

    public ArrayList<String> addressesList;

    public LadleItemHandler ladleInv;
    public ItemStack heldBox;
    private final LazyOptional<IItemHandler> ladleProvider;

    public static final int CYCLE = 20;
    public int animationTicks;
    public boolean animationInward;

    public LabellingStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        addressesList = new ArrayList<>();
        ladleInv = new LadleItemHandler(this);
        heldBox = ItemStack.EMPTY;
        ladleProvider = LazyOptional.of(() -> ladleInv);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        animationTicks = compound.getInt("AnimationTicks");
        heldBox = ItemStack.of(compound.getCompound("HeldBox"));

        ListTag list = compound.getList("AddrsList", Tag.TAG_COMPOUND);
        if (!list.isEmpty()) {
            addressesList.clear();
            for (int i = 0; i < list.size(); i++) {
                CompoundTag item = list.getCompound(i);
                if (item.contains("Address", Tag.TAG_STRING))
                    addressesList.add(i, item.getString("Address"));
            }
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.putInt("AnimationTicks", animationTicks);
        compound.put("HeldBox", heldBox.serializeNBT());

        ListTag list = new ListTag();
        for (String address : addressesList) {
            if (address != null) {
                CompoundTag addressTag = new CompoundTag();
                addressTag.putString("Address", address);
                list.add(addressTag);
            }
        }
        if (!list.isEmpty())
            compound.put("AddrsList", list);
    }

    @Override
    public void tick() {
        super.tick();

        if (buttonCooldown > 0)
            buttonCooldown--;


        if (level.isClientSide) {
            if (animationTicks == CYCLE - (animationInward ? 5 : 1))
                AllSoundEvents.PACKAGER.playAt(level, worldPosition, 1, 1, true);
            if (animationTicks == (animationInward ? 1 : 5))
                level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
                        true);
        }

        if (animationTicks > 0)
            animationTicks--;

        if (animationTicks == 0 && !level.isClientSide()) {
            if (!heldBox.isEmpty())
                attemptToSend();
            setChanged();
        }
    }

    @Override
    public void lazyTick() {
        if (level.isClientSide())
            return;
        if (!redstonePowered)
            return;
        redstonePowered = getBlockState().getOptionalValue(LabellingStationBlock.POWERED)
                .orElse(false);
        if (!redstoneModeActive())
            return;
        updateClipBoardAddresses();
    }

    public void activate() {
        redstonePowered = true;
        setChanged();

        if (!redstoneModeActive())
            return;

        updateClipBoardAddresses();
        attemptToSend();

        buttonCooldown = 20;
    }

    public boolean redstoneModeActive() {
        return !getBlockState().getOptionalValue(LabellingStationBlock.LINKED)
                .orElse(false);
    }

    protected void updateClipBoardAddresses() {
        addressesList.clear();
        for (Direction side : Iterate.directions) {
            ArrayList<String> addresses = getClipBoardAddresses(side);
            if (addresses == null || addresses.isEmpty())
                continue;
            addressesList = addresses;
        }
    }

    protected ArrayList<String> getClipBoardAddresses(Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
        if (!(blockEntity instanceof ClipboardBlockEntity cbe))
            return null;

        List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(cbe.dataContainer);
        if (pages.isEmpty())
            return null;

        ArrayList<String> addresses = new ArrayList<>();
        pages.forEach(page -> page.forEach(entry -> {
            String string = entry.text.getString();
            if (entry.checked)
                return;
            if (!string.startsWith("#") || string.length() <= 1)
                return;
            String address = string.substring(1);
            if (address.isBlank())
                return;
            addresses.add(address.trim());
        }));
        if (!addresses.isEmpty())
            return addresses;

        return null;
    }

    public void attemptToSend() {
        if (heldBox.isEmpty() || animationTicks != 0)
            return;

        updateClipBoardAddresses();

        LadleItem.clearAddress(heldBox);
        LadleItem.clearRemainAddrs(heldBox);

        ArrayList<String> addresses = addressesList;
        if (!addresses.isEmpty()) {
            LadleItem.addAddress(heldBox, addresses.remove(0));
            LadleItem.addRemainAddrs(heldBox, addresses);
        }

        //BlockPos linkPos = getLinkPos();
        /*if (linkPos != null && level.getBlockEntity(linkPos) instanceof PackagerLinkBlockEntity plbe)
            plbe.behaviour.deductFromAccurateSummary(extractedItems);*/

        /*if (!heldBox.isEmpty() || animationTicks != 0) {
            queuedExitingPackages.add(new BigItemStack(createdBox, 1));
            return;
        }*/

        AllSoundEvents.STOCK_TICKER_TRADE.playOnServer(level, getBlockPos());

        animationInward = false;
        animationTicks = 0;
        ladleInv.allowExtract();
    }

    public void boxArrived() {
        animationInward = false;
        animationTicks = CYCLE;
        notifyUpdate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ladleProvider.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return ladleProvider.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, ladleInv);
    }
}