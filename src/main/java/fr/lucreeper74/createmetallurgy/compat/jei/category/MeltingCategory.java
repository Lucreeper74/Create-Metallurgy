package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryTopElement;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class MeltingCategory extends FoundryBasinCategory {
    private final FoundryTopElement castingtop = new FoundryTopElement();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public MeltingCategory(Info<FoundryBasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(FoundryBasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        drawProcessTime(recipe, matrixStack, 55);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);
        castingtop.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    }

    protected void drawProcessTime(FoundryBasinRecipe recipe, PoseStack poseStack, int y) {
        int duration = recipe.getProcessingDuration();

        if(duration > 0) {
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", (float) duration / 20).withStyle(ChatFormatting.GRAY);
            Font renderer = Minecraft.getInstance().font;
            int stringWidth = renderer.width(timeString) + 5;
            renderer.draw(poseStack, timeString, ((float) getWidth() / 2) - stringWidth, y, 0xffffff);
        }
    }
}
