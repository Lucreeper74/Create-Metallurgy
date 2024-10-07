package fr.lucreeper74.createmetallurgy.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CastingItemRenderTypeBuffer implements MultiBufferSource {
    private final MultiBufferSource inner;
    private final int alpha, red, green, blue;

    public CastingItemRenderTypeBuffer(MultiBufferSource inner, int alphaItem, int alphaFluid) {
        this.inner = inner;
        // alpha is a direct fade from 0 to 255
        this.alpha = Mth.clamp(alphaItem, 0, 0xFF);
        // RGB based on fluid opacity, fades from 0xB06020 tint to 0xFFFFFF
        alphaFluid = Mth.clamp(alphaFluid, 0, 0xFF);
        this.red   = 0xFF - (alphaFluid * (0xFF - 0xB0) / 0xFF);
        this.green = 0xFF - (alphaFluid * (0xFF - 0x60) / 0xFF);
        this.blue  = 0xFF - (alphaFluid * (0xFF - 0x20) / 0xFF);
    }
    @NotNull
    @Override
    public VertexConsumer getBuffer(RenderType type) {
        if (alpha < 255)
            type = RenderType.translucent();

        return new TintedVertexBuilder(inner.getBuffer(type), red, green, blue, alpha);
    }
}
