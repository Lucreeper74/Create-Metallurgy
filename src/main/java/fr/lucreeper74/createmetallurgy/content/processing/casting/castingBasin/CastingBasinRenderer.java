package fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import fr.lucreeper74.createmetallurgy.utils.CastingItemRenderTypeBuffer;
import fr.lucreeper74.createmetallurgy.utils.ColoredFluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CastingBasinRenderer extends SmartBlockEntityRenderer<CastingBasinBlockEntity> {
    public CastingBasinRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private CastingBasinRecipe recipe;

    @Override
    protected void renderSafe(CastingBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        List<Recipe<?>> recipes = be.getMatchingRecipes();
        if (!recipes.isEmpty()) recipe = (CastingBasinRecipe) recipes.get(0);

        //Render Fluids
        SmartFluidTankBehaviour tank = be.inputTank;
        if (tank == null)
            return;

        int fluidOpacity = 255;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level > 0.01F) {

            float min = 2f / 16f;
            float yOffset = (13f / 16f) * level;

            ms.pushPose();
            ms.translate(0, yOffset, 0);

            if (be.running) {
                int timer = be.processingTick;
                int totalTime = recipe.getProcessingDuration();

                if (timer > 0 && totalTime > 0) fluidOpacity = 255 * timer / totalTime;
            }

            ColoredFluidRenderer.renderFluidBox(fluidStack,
                    2f / 16f, min - yOffset, 2f / 16f,
                    14f / 16f, min, 14f / 16f, buffer, ms, light, ColoredFluidRenderer.RGBAtoColor(255, 255, 255, 255), false);

            ms.popPose();
        }

        //Render Items
        ms.pushPose();

        if (be.running) {
            MultiBufferSource bufferOut = new CastingItemRenderTypeBuffer(buffer, 255 - fluidOpacity, fluidOpacity);
            renderItem(ms, bufferOut, light, overlay, recipe.getResultItem(be.getLevel().registryAccess()).copy());
        }

        renderItem(ms, buffer, light, overlay, be.inv.getItem(0));
        ms.popPose();
    }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        if(stack.getItem() instanceof BlockItem) {
            ms.translate(.5f, 0, .5f);
            ms.scale(3.1f, 3.1f, 3.1f);
        } else {
            ms.translate(.5f, .5f, .5f);
            ms.scale(1, 1, 1);
        }

        mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
    }
}