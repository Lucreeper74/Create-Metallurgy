package fr.lucreeper74.createmetallurgy.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class TintedVertexBuilder implements VertexConsumer {
    /**
     * Base vertex builder
     */
    private final VertexConsumer inner;
    /**
     * Tint color from 0-255
     */
    private final int tintRed, tintGreen, tintBlue, tintAlpha;

    public TintedVertexBuilder(VertexConsumer inner, int tintRed, int tintGreen, int tintBlue, int tintAlpha) {
        this.inner = inner;
        this.tintRed = tintRed;
        this.tintGreen = tintGreen;
        this.tintBlue = tintBlue;
        this.tintAlpha = tintAlpha;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return inner.addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return inner.setColor((red * tintRed) / 0xFF, (green * tintGreen) / 0xFF, (blue * tintBlue) / 0xFF, (alpha * tintAlpha) / 0xFF);
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return inner.setUv(u, v);
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return inner.setUv1(u, v);
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return inner.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return inner.setNormal(x, y, z);
    }
}
