package fr.lucreeper74.createmetallurgy.compat.jei.category.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import mezz.jei.api.gui.drawable.IDrawable;

public class CastingInTableElement implements IDrawable {
    @Override
    public int getWidth() {
        return 50;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset, 0);
        AllGuiTextures.JEI_SHADOW.render(poseStack, -16, 13);
        poseStack.translate(-2, 18, 0);

        GuiGameElement.of(AllBlocks.CASTING_TABLE_BLOCK.getDefaultState())
                .rotateBlock(22.5, 22.5, 0)
                .scale(22)
                .render(poseStack);
        poseStack.popPose();
    }
}