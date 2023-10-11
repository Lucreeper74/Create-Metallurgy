package fr.lucreeper74.createmetallurgy.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import fr.lucreeper74.createmetallurgy.registries.AllPModels;
import net.minecraft.util.Mth;

import static fr.lucreeper74.createmetallurgy.content.processing.glassedalloyertop.GlassedCastingTopBlock.UNDER_FOUNDRY_MIXER;

public class AnimatedFoundryMixer extends AnimatedKinetics {
    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(cogwheel())
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(AllBlocks.FOUNDRY_MIXER_BLOCK.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(matrixStack);

        float animation = ((Mth.sin(AnimationTickHolder.getRenderTime() / 32f) + 1) / 5) + .5f;

        blockElement(AllPModels.FOUNDRY_MIXER_POLE)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(AllPModels.FOUNDRY_MIXER_HEAD)
                .rotateBlock(0, getCurrentAngle() * 4, 0)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(AllBlocks.GLASSED_ALLOYER_TOP_BLOCK.getDefaultState().setValue(UNDER_FOUNDRY_MIXER, true))
                .atLocal(0, .65, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(AllBlocks.FOUNDRY_BASIN_BLOCK.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }

}
