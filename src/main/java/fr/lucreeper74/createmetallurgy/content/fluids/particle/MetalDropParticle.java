package fr.lucreeper74.createmetallurgy.content.fluids.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalDropParticle extends TextureSheetParticle {
    private static final float SPARK_SPEED_FACTOR = .05f;
    private static final float DROP_BOUNCINESS = .3f;

    protected MetalDropParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.gravity = 0.98f;
        this.friction = 0.998f;
        this.xd *= 0.8f;
        this.yd *= 0.8f;
        this.zd *= 0.8f;
        this.yd = (this.random.nextFloat() * .3f + .05f);
        this.quadSize *= (this.random.nextFloat() + .1f);
        this.lifetime = (int) (16.0 / (Math.random() * .8f + .2f));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        int k = i >> 16 & 0xFF;
        return 240 | k << 16;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float f = ((float) this.age + scaleFactor) / (float) this.lifetime;
        return this.quadSize * (1f - f * f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed)
            return;

        if (this.onGround && this.yd < 0)
            this.yd *= -DROP_BOUNCINESS;

        float f = (float) this.age / (float) this.lifetime;
        if (this.random.nextFloat() > f) {
            double spark_xd = this.xd + (this.random.nextDouble() * 2 - 1) * SPARK_SPEED_FACTOR;
            double spark_yd = this.yd + (this.random.nextDouble() * 2 - 1) * SPARK_SPEED_FACTOR;
            double spark_zd = this.zd + (this.random.nextDouble() * 2 - 1) * SPARK_SPEED_FACTOR;
            this.level.addParticle(new MetalSparkParticleData(1f), this.x, this.y, this.z, spark_xd, spark_yd, spark_zd);
        }
    }

    public static class Factory implements ParticleProvider<MetalDropParticleData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(
                MetalDropParticleData data,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            MetalDropParticle metalDropParticle = new MetalDropParticle(level, x, y, z);
            metalDropParticle.pickSprite(this.sprite);
            return metalDropParticle;
        }
    }
}
