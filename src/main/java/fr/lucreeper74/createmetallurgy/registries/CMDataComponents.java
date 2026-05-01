package fr.lucreeper74.createmetallurgy.registries;

import com.mojang.serialization.Codec;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class CMDataComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateMetallurgy.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> LADLE_FLUID_CONTENT = DATA_COMPONENTS.register(
            "ladle_content", () -> {
                DataComponentType.Builder<SimpleFluidContent> builder = DataComponentType.builder();
                return builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build();
            });

    public static final DataComponentType<List<String>> LADLE_REMAIN_ADDRESSES = register(
            "ladle_remain_address",
            builder -> builder.persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8))
    );

    public static final DataComponentType<FluidStack> LADLE_FILTER_FLUID = register(
            "ladle_filter_fluid",
            builder -> builder.persistent(FluidStack.CODEC).networkSynchronized(FluidStack.STREAM_CODEC)
    );

    public static final DataComponentType<Integer> LADLE_FILTER_FLUID_AMOUNT = register(
            "ladle_filter_fluid_amount",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<Integer> LADLE_FILTER_COMPARATOR = register(
            "ladle_filter_comparator",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
