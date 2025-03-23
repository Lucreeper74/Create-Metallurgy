package fr.lucreeper74.createmetallurgy.compat.jei.category.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import mezz.jei.api.gui.drawable.IDrawable;

import static com.simibubi.create.compat.jei.category.animations.AnimatedKinetics.defaultBlockElement;

public class FoundryElement implements IDrawable {

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
        poseStack.mulPose(Vector3f.YP.rotationDegrees(22.5f + 180f));
        int scale = 23;

        defaultBlockElement(CMPartialModels.JEI_CURCIBLE_2X2)
                .atLocal(-.4f, 1.5f, -1.6f)
                .scale(scale)
                .render(poseStack);
        poseStack.popPose();
    }
}