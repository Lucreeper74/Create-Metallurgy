package fr.lucreeper74.createmetallurgy.compat.jei.category.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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
        poseStack.translate(xOffset, yOffset, 200);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;

        GuiGameElement.of(AllBlocks.CASTING_TABLE_BLOCK.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(poseStack);
        poseStack.popPose();
    }
}
