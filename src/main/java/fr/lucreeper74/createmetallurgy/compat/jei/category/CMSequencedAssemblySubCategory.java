package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import fr.lucreeper74.createmetallurgy.compat.jei.category.animations.AnimatedBeltGrinder;
import net.minecraft.client.gui.GuiGraphics;

public abstract class CMSequencedAssemblySubCategory extends SequencedAssemblySubCategory {
    public CMSequencedAssemblySubCategory(int width) {
        super(width);
    }

    public static class AssemblyGrinding extends CMSequencedAssemblySubCategory {

        AnimatedBeltGrinder grinder;

        public AssemblyGrinding() {
            super(25);
            grinder = new AnimatedBeltGrinder();
        }

        @Override
        public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(0, 51.5f, 0);
            ms.scale(.6f, .6f, .6f);
            grinder.draw(graphics, getWidth() / 2, 30);
            ms.popPose();
        }
    }
}