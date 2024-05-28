package fr.lucreeper74.createmetallurgy.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder.BeltGrinderBlock;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedBeltGrinder extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.translate(0, 0, 200);
        matrixStack.translate(2, 22, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f + 90));
        int scale = 25;

        blockElement(shaft(Direction.Axis.X))
                .rotateBlock(-getCurrentAngle(), 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CMBlocks.BELT_GRINDER_BLOCK.getDefaultState()
                .setValue(BeltGrinderBlock.HORIZONTAL_FACING, Direction.WEST))
                .rotateBlock(0, 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CMPartialModels.GRINDER_BELT)
                .rotateBlock(0, -90, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}