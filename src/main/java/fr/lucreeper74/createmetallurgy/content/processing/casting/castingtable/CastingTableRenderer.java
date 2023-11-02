package fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import fr.lucreeper74.createmetallurgy.utils.CastingItemRenderTypeBuffer;
import fr.lucreeper74.createmetallurgy.utils.ColoredFluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlock.FACING;

public class CastingTableRenderer extends SmartBlockEntityRenderer<CastingTableBlockEntity> {
    public CastingTableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private CastingTableRecipe recipe;

    @Override
    protected void renderSafe(CastingTableBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        List<Recipe<?>> recipes = be.getMatchingRecipes();
        if(!recipes.isEmpty()) recipe = (CastingTableRecipe) recipes.get(0);

        //Render Fluids
        SmartFluidTankBehaviour tank = be.inputTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        int fluidOpacity = 255;

        if (!fluidStack.isEmpty() && level > 0.01F) {
            float min = 13f / 16f;
            float yOffset = (.8f / 16f) * level;

            ms.pushPose();
            ms.translate(0, yOffset, 0);

            if (be.running) {
                int timer = be.processingTick;
                int totalTime = recipe.getProcessingDuration();

                if (timer > 0 && totalTime > 0) fluidOpacity = 255 * timer / totalTime;
            }

            ColoredFluidRenderer.renderFluidBox(fluidStack,
                    2f / 16f, min - yOffset, 2f / 16f,
                    14f / 16f, min, 14f / 16f, buffer, ms, light, ColoredFluidRenderer.RGBAtoColor(255, 255, 255, fluidOpacity), false);

            ms.popPose();
        }

        //Render Items
        ms.pushPose();

        ms.translate(.5f, 0, .5f);
        ms.mulPose(Vector3f.YP.rotationDegrees(-90f * (be.getBlockState().getValue(FACING).get2DDataValue())));
        ms.translate(-.5f, 0, -.5f);

        ms.translate(.5f, 13.5f / 16f, 11 / 16f);
        ms.scale(1.5f, 1.5f, 1.5f);
        ms.mulPose(Vector3f.XP.rotationDegrees(-90f));

        if (be.running) {
            MultiBufferSource bufferOut = new CastingItemRenderTypeBuffer(buffer, 255 - fluidOpacity, fluidOpacity);
            renderItem(ms, bufferOut, light, overlay, recipe.getResultItem().copy());
        }

        renderItem(ms, buffer, light, overlay, be.inv.getItem(0));
        renderItem(ms, buffer, light, overlay, be.moldInv.getItem(0));
        ms.popPose();
    }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(stack, ItemTransforms.TransformType.GROUND, light, overlay, ms, buffer, 0);
    }
}