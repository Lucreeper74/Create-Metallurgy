package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class FoundryRecipeParams extends ProcessingRecipeParams {

    public static final int DEFAULT_MIN_HEAT = -25;
    public static final int DEFAULT_MAX_HEAT = 50;

    public static MapCodec<FoundryRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(FoundryRecipeParams::new).forGetter(Function.identity()),
            Codec.INT.optionalFieldOf("min_heat_req", DEFAULT_MIN_HEAT).forGetter(FoundryRecipeParams::getMinHeatRequirement),
            Codec.INT.optionalFieldOf("max_heat_req", DEFAULT_MAX_HEAT).forGetter(FoundryRecipeParams::getMaxHeatRequirement)
    ).apply(instance, (params, minHeatRequirement, maxHeatRequirement) -> {
        params.minHeatRequirement = minHeatRequirement;
        params.maxHeatRequirement = maxHeatRequirement;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, FoundryRecipeParams> STREAM_CODEC = streamCodec(FoundryRecipeParams::new);

    protected int minHeatRequirement;
    protected int maxHeatRequirement;

    protected FoundryRecipeParams() {
        super();
        minHeatRequirement = DEFAULT_MIN_HEAT;
        maxHeatRequirement = DEFAULT_MAX_HEAT;
    }

    protected int getMinHeatRequirement() {
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
