package fr.lucreeper74.createmetallurgy.content.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipe;
import fr.lucreeper74.createmetallurgy.content.casting.table.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.utils.CastingItemRenderTypeBuffer;
import fr.lucreeper74.createmetallurgy.utils.ColoredFluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.casting.CastingBlock.FACING;

public class CastingBlockRenderer extends SmartBlockEntityRenderer<CastingBlockEntity> {
    public CastingBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private CastingRecipe recipe;

    @Override
    protected void renderSafe(CastingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        List<Recipe<?>> recipes = be.getMatchingRecipes();
        if (!recipes.isEmpty()) recipe = (CastingRecipe) recipes.get(0);

        //Render Fluids
        SmartFluidTankBehaviour tank = be.inputTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        int fluidOpacity = 255;

        float min, yOffset;

        if (!fluidStack.isEmpty() && level > 0.01F) {
            if(be instanceof CastingTableBlockEntity) {
                min = 13f / 16f;
                yOffset = (.8f / 16f) * level;
            } else {
                min = 2f / 16f;
                yOffset = (13f / 16f) * level;
            }

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
        if (be.running) {
            MultiBufferSource bufferOut = new CastingItemRenderTypeBuffer(buffer, 255 - fluidOpacity, fluidOpacity);
            renderItem(be, ms, bufferOut, light, overlay, recipe.getResultItem(be.getLevel().registryAccess()).copy());
        }

        renderItem(be, ms, buffer, light, overlay, be.inv.getItem(0));
        renderItem(be, ms, buffer, light, overlay, be.moldInv.getItem(0));
    }

    protected void renderItem(CastingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        ms.pushPose();
        if (be instanceof CastingTableBlockEntity) {
            ms.translate(.5f, 0, .5f);
            ms.mulPose(Axis.YP.rotationDegrees(-90f * (be.getBlockState().getValue(FACING).get2DDataValue())));
            ms.translate(-.5f, 0, -.5f);

            ms.translate(.5f, 13.5f / 16f, 11 / 16f);
            ms.scale(1.5f, 1.5f, 1.5f);
            ms.mulPose(Axis.XP.rotationDegrees(-90f));

        } else {

            if (stack.getItem() instanceof BlockItem) {
                ms.translate(.5f, 0, .5f);
                ms.scale(3.1f, 3.1f, 3.1f);
            } else {
                ms.translate(.5f, .5f, .5f);
                ms.scale(1, 1, 1);
            }
        }

        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
        ms.popPose();
    }
}