package fr.lucreeper74.createmetallurgy.utils;

import com.mojang.math.Axis;
import com.simibubi.create.content.fluids.FluidMesh;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

public class CMFluidVisual {
        private final SmartRecycler<TextureAtlasSprite, TransformedInstance> surface;
        private final Direction[] sides;
        private final boolean renderGasesFromTop;

        public CMFluidVisual(VisualizationContext context, boolean renderBottom, boolean renderGasesFromTop) {
            surface = new SmartRecycler<>(key ->
                    context.instancerProvider()
                            .instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(key, 1))
                            .createInstance());
            sides = renderBottom ? Iterate.directions : Arrays.copyOfRange(Iterate.directions, 1, Iterate.directions.length);
            this.renderGasesFromTop = renderGasesFromTop;
        }

        public TransformedInstance[] setupBuffers(FluidStack fluidStack, int start) {
            if (fluidStack.isEmpty()) return null;

            TransformedInstance[] buffers = new TransformedInstance[start + sides.length];

            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            var atlas = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite stillTexture = atlas.apply(clientFluid.getStillTexture(fluidStack));

            for (int i = 0; i < sides.length; i++) {
                buffers[start + i] = surface.get(stillTexture);
                buffers[start + i].colorArgb(clientFluid.getTintColor(fluidStack));
            }

            return buffers;
        }

        public void setupBuffer(FluidStack fluidStack, int capacity, TransformedInstance buffer, int index, float width, float height) {
            var side = sides[index];

            width -= 1 / 256f;

            float fillFactor = (float) fluidStack.getAmount() / capacity;

            buffer.translateY(-19f / 16);

            if (side.getAxis().isHorizontal()) {
                buffer.translateY(fillFactor * height / 2);
                buffer.scaleY(fillFactor / 2);
            } else {
                buffer.translateY(fillFactor * height / 2 + height / 2);
            }

            float horizontalOffset = width / 4 + width;
            horizontalOffset *= side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
            if (side.getAxis() == Direction.Axis.X) {
                buffer.translateX(horizontalOffset);
            } else if (side.getAxis() == Direction.Axis.Z) {
                buffer.translateZ(horizontalOffset);
            }

            buffer.scale(.5f);

            if (renderGasesFromTop && fluidStack.getFluid().getFluidType().isLighterThanAir()) {
                buffer.rotateDegrees(180, Axis.XP);
            }
            buffer.rotateTo(Direction.UP, side);

            if (side.getAxis().isVertical())
                buffer.scale(width);

            if (side.getAxis() == Direction.Axis.X) {
                buffer.scaleZ(width);
            } else if (side.getAxis() == Direction.Axis.Z) {
                buffer.scaleX(width);
            }
        }

        public void begin() {
            surface.resetCount();
        }

        public void end() {
            surface.discardExtra();
        }

        public void delete() {
            surface.delete();
        }
}