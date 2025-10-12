package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum CMPackets {

    /* Client to server */
    CONFIGURE_LADLE_FILTER(LadleFilterScreenPacket.class, LadleFilterScreenPacket::new, PLAY_TO_SERVER),
    ;

    public static final ResourceLocation CHANNEL_NAME = CreateMetallurgy.genRL("main");
    public static final int NETWORK_VER = 1;
    public static final String NETWORK_VER_STR = String.valueOf(NETWORK_VER);

    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
            .serverAcceptedVersions(NETWORK_VER_STR::equals)
            .clientAcceptedVersions(NETWORK_VER_STR::equals)
            .networkProtocolVersion(() -> NETWORK_VER_STR)
            .simpleChannel();

    private CMPacketType<?> packetType;

    <T extends SimplePacketBase> CMPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packetType = new CMPacketType<>(type, factory, direction);
    }

    public static void registerPackets() {
        for (CMPackets packet : values())
            packet.packetType.register();
    }

    private static class CMPacketType<T extends SimplePacketBase> {
        private static int index = 0;
        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private CMPacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context))
                    context.setPacketHandled(true);
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            INSTANCE.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }
}
