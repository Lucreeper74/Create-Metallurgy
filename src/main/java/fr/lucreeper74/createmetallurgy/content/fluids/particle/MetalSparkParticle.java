package fr.lucreeper74.createmetallurgy.content.fluids.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalSparkParticle extends SimpleAnimatedParticle {
    private static final float SPARK_BOUNCINESS = .5f;

    protected MetalSparkParticle(ClientLevel level, double x, double y, double z,
                                 double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites, 1f);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= (this.random.nextFloat() * .2f + .2f);
        this.lifetime = 15 + this.random.nextInt(5);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.removed)
            return;

        if (this.onGround && this.yd < 0)
            this.yd *= -SPARK_BOUNCINESS;

        if (this.age > (3 * this.lifetime / 4)) {
            float f = ((float) this.age / (float) this.lifetime);
            this.setColor(.6f + f, .4f + f, .4f + f);
        }
    }

    public static class Factory implements ParticleProvider<MetalSparkParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(MetalSparkParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new MetalSparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}