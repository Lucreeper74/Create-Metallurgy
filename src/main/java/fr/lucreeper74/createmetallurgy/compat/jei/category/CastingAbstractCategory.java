package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CastingAbstractCategory<T extends CastingRecipe> extends CreateRecipeCategory<T> {
    public CastingAbstractCategory(Info<T> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        FluidIngredient fluidIngredient = recipe.getFluidIngredient();
        addFluidSlot(builder, 15, 6, fluidIngredient);

        Ingredient mold = recipe.getIngredient();
        if (!mold.isEmpty())
            builder
                    .addSlot(RecipeIngredientRole.INPUT, 15, 26)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredients(mold);

        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(getResultItem(recipe));
    }

    protected void drawCastingTime(T recipe, GuiGraphics graphics, int y) {
        int duration = recipe.getProcessingDuration();
        if(duration > 0) {
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", duration / 20f).withStyle(ChatFormatting.GRAY);
            Font renderer = Minecraft.getInstance().font;
            int stringWidth = renderer.width(timeString);

            graphics.drawString(renderer, timeString, (115 - stringWidth), y, 0xffffff, false);
        }
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();

        if (!recipe.getIngredient().isEmpty() && recipe.isMoldConsumed()) {
            int minX = 14;
            int maxX = minX + 18;
            int minY = 44;
            int maxY = minY + 18;

            if (mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
                tooltip.add(CMLang.translateDirect("recipe.casting.mold_consumed").withStyle(ChatFormatting.RED));
                return tooltip;
            }
        }
        return tooltip;
    }
}