package fr.lucreeper74.createmetallurgy.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import fr.lucreeper74.createmetallurgy.content.belt_grinder.BeltGrinderBlock;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.minecraft.core.Direction;

public class AnimatedBeltGrinder extends AnimatedKinetics {
    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.translate(0, 0, 200);
        matrixStack.translate(2, 22, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f + 90));
        int scale = 25;

        blockElement(shaft(Direction.Axis.X))
                .rotateBlock(-getCurrentAngle(), 0, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(CMBlocks.BELT_GRINDER_BLOCK.getDefaultState()
                .setValue(BeltGrinderBlock.HORIZONTAL_FACING, Direction.WEST))
                .rotateBlock(0, 0, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(CMPartialModels.GRINDER_BELT)
                .rotateBlock(0, -90, 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }
}