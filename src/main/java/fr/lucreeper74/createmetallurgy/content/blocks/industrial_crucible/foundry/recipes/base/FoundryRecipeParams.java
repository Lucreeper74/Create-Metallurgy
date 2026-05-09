package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;
import java.util.function.Supplier;

public class FoundryRecipeParams extends ProcessingRecipeParams {

    public static final int DEFAULT_MIN_HEAT = -25;
    public static final int DEFAULT_MAX_HEAT = 50;

    public static MapCodec<FoundryRecipeParams> CODEC = foundryCodec(FoundryRecipeParams::new);
    public static StreamCodec<RegistryFriendlyByteBuf, FoundryRecipeParams> STREAM_CODEC = streamCodec(FoundryRecipeParams::new);

    protected int minHeatRequirement;
    protected int maxHeatRequirement;

    protected FoundryRecipeParams() {
        super();
        minHeatRequirement = DEFAULT_MIN_HEAT;
        maxHeatRequirement = DEFAULT_MAX_HEAT;
    }

    protected static <P extends FoundryRecipeParams> MapCodec<P> foundryCodec(Supplier<P> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ProcessingRecipeParams.codec(factory).forGetter(Function.identity()),
                Codec.INT.optionalFieldOf("minHeatRequirement", DEFAULT_MIN_HEAT).forGetter(FoundryRecipeParams::getMinHeatRequirement),
                Codec.INT.optionalFieldOf("maxHeatRequirement", DEFAULT_MAX_HEAT).forGetter(FoundryRecipeParams::getMaxHeatRequirement)
        ).apply(instance, (params, minHeatRequirement, maxHeatRequirement) -> {
            params.minHeatRequirement = minHeatRequirement;
            params.maxHeatRequirement = maxHeatRequirement;
            return params;
        }));
    }

    public int getMinHeatRequirement() {
        return minHeatRequirement;
    }

    public int getMaxHeatRequirement() {
        return maxHeatRequirement;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.INT.encode(buffer, minHeatRequirement);
        ByteBufCodecs.INT.encode(buffer, maxHeatRequirement);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        minHeatRequirement = ByteBufCodecs.INT.decode(buffer);
        maxHeatRequirement = ByteBufCodecs.INT.decode(buffer);
    }
}
