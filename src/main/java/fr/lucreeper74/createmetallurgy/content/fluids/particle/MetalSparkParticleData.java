package fr.lucreeper74.createmetallurgy.content.fluids.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import fr.lucreeper74.createmetallurgy.registries.CMParticleTypes;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MetalSparkParticleData implements ParticleOptions, ICustomParticleDataWithSprite<MetalSparkParticleData> {

    public static final MapCodec<MetalSparkParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(Codec.FLOAT.fieldOf("speed")
                    .forGetter(p -> p.speed))
            .apply(i, MetalSparkParticleData::new));

    public static final StreamCodec<ByteBuf, MetalSparkParticleData> STREAM_CODEC = ByteBufCodecs.FLOAT.map(
            MetalSparkParticleData::new, p -> p.speed);

    float speed;

    public MetalSparkParticleData(float speed) {
        this.speed = speed;
    }

    public MetalSparkParticleData() {
        this(0);
    }

    @Override
    public ParticleType<?> getType() {
        return CMParticleTypes.MOLTEN_METAL_SPARK.get();
    }

    @Override
    public MapCodec<MetalSparkParticleData> getCodec(ParticleType<MetalSparkParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<MetalSparkParticleData> getMetaFactory() {
        return MetalSparkParticle.Factory::new;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, MetalSparkParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}