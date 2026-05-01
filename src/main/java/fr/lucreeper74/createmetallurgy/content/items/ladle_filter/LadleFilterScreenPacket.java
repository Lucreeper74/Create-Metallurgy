package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.AllPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record LadleFilterScreenPacket(Option option, @Nullable CompoundTag data) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, LadleFilterScreenPacket> STREAM_CODEC = StreamCodec.composite(
            Option.STREAM_CODEC, LadleFilterScreenPacket::option,
            CatnipStreamCodecBuilders.nullable(ByteBufCodecs.COMPOUND_TAG), LadleFilterScreenPacket::data,
            LadleFilterScreenPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_FILTER;
    }

    @Override
    public void handle(ServerPlayer player) {
        CompoundTag tag = this.data == null ? new CompoundTag() : this.data;

        if (player.containerMenu instanceof LadleFilterMenu menu) {
            switch (option) {
                case UPDATE_ADDRESS:
                    menu.address = tag.getString("Address");
                    break;
                case UPDATE_PERCENT:
                    menu.filledAmount = tag.getInt("FilledAmount");
                    menu.comparator = tag.getInt("Comparator");
                    break;
                case UPDATE_FLUID:
                    menu.fluidFilter = FluidStack.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("FluidFilter")).result().orElse(FluidStack.EMPTY);
                    break;
            }
        }
    }

    public enum Option {
        UPDATE_ADDRESS, UPDATE_PERCENT, UPDATE_FLUID;

        public static final StreamCodec<ByteBuf, Option> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Option.class);
    }
}
