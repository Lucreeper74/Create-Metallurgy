package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public abstract class CastingAbstractCategory<T extends CastingRecipe> extends CreateRecipeCategory<T> {
    public CastingAbstractCategory(Info<T> info) {
        super(info);
    }

    protected void drawCastingTime(T recipe, PoseStack poseStack, int y) {
        int duration = recipe.getProcessingDuration();

        if(duration > 0) {
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", (float) duration / 20).withStyle(ChatFormatting.GRAY);
            Font renderer = Minecraft.getInstance().font;
            int stringWidth = renderer.width(timeString);
            renderer.draw(poseStack, timeString, (115 - stringWidth), y, 0xffffff);
        }
    }
}