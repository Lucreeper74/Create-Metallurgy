package fr.lucreeper74.createmetallurgy.compat.jei.category.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class CastingInBasinElement implements IDrawable {
    @Override
    public int getWidth() {
        return 50;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset, 0);
        AllGuiTextures.JEI_SHADOW.render(graphics, -16, 13);
        poseStack.translate(-2, 18, 0);

        GuiGameElement.of(CMBlocks.CASTING_BASIN_BLOCK.getDefaultState())
                .rotateBlock(22.5, 22.5, 0)
                .scale(22)
                .render(graphics);
        poseStack.popPose();
    }
}