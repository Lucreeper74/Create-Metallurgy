package fr.lucreeper74.createmetallurgy.content.fluids.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import fr.lucreeper74.createmetallurgy.registries.CMParticleTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MetalDropParticleData implements ParticleOptions, ICustomParticleDataWithSprite<MetalDropParticleData> {

    public static final MapCodec<MetalDropParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(Codec.FLOAT.fieldOf("speed")
                    .forGetter(p -> p.speed))
            .apply(i, MetalDropParticleData::new));

    public static final StreamCodec<ByteBuf, MetalDropParticleData> STREAM_CODEC = ByteBufCodecs.FLOAT.map(
            MetalDropParticleData::new, p -> p.speed);

    float speed;

    public MetalDropParticleData(float speed) {
        this.speed = speed;
    }

    public MetalDropParticleData() {
        this(0);
    }

    @Override
    public ParticleType<?> getType() {
        return CMParticleTypes.MOLTEN_METAL_DROP.get();
    }

    @Override
    public MapCodec<MetalDropParticleData> getCodec(ParticleType<MetalDropParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<MetalDropParticleData> getMetaFactory() {
        return MetalDropParticle.Factory::new;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, MetalDropParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}