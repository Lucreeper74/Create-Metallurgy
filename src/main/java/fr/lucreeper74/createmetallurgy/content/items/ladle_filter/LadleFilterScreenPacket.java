package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

public class LadleFilterScreenPacket extends SimplePacketBase {

    public enum Type {
        UPDATE_ADDRESS, UPDATE_PERCENT, UPDATE_FLUID
    }

    private final Type type;
    private final CompoundTag NBTdata;

    public LadleFilterScreenPacket(FriendlyByteBuf buffer) {
        type = Type.values()[buffer.readInt()];
        NBTdata = buffer.readNbt();
    }

    public LadleFilterScreenPacket(Type type, CompoundTag data) {
        this.type = type;
        this.NBTdata = data;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(type.ordinal());
        buffer.writeNbt(NBTdata);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;

            if (player.containerMenu instanceof LadleFilterMenu menu) {
                switch (type) {
                    case UPDATE_ADDRESS:
                        menu.address = NBTdata.getString("Address");
                        break;
                    case UPDATE_PERCENT:
                        menu.filledAmount = NBTdata.getInt("FilledAmount");
                        menu.comparator = NBTdata.getInt("Comparator");
                        break;
                    case UPDATE_FLUID:
                        menu.fluidFilter = FluidStack.loadFluidStackFromNBT(NBTdata.getCompound("FluidFilter"));
                        break;
                }
            }
        });
        return true;
    }
}
