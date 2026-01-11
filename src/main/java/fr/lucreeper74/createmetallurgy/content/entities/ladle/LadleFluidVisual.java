package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.simibubi.create.content.fluids.FluidMesh;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

public class LadleFluidVisual {
    private static final float FLUID_SPRITE_WIDTH = 4f;
    public static final float MINY_LADLE = -7 / 16f;

    private final SmartRecycler<TextureAtlasSprite, TransformedInstance> fluidSurface;

    public LadleFluidVisual(VisualizationContext ctx) {
        fluidSurface = new SmartRecycler<>(key ->
                ctx.instancerProvider()
                        .instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(key, 1 / FLUID_SPRITE_WIDTH))
                        .createInstance());
    }

    public TransformedInstance setupInstance(FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            return null;

        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite stillTexture = atlas.apply(clientFluid.getStillTexture(fluidStack));

        TransformedInstance instance = fluidSurface.get(stillTexture);
        instance.colorArgb(clientFluid.getTintColor(fluidStack));

        instance.setIdentityTransform()
                .center()
                .setChanged();

        return instance;
    }

    public void render(FluidStack fluidStack, int capacity, TransformedInstance buffer, float height) {
        float fillFactor = (float) fluidStack.getAmount() / capacity;

        float fluidLevel = fillFactor * height;

        buffer.translateY(MINY_LADLE + fluidLevel);
    }

    public void begin() {
        fluidSurface.resetCount();
    }

    public void end() {
        fluidSurface.discardExtra();
    }

    public void delete() {
        fluidSurface.delete();
    }
}