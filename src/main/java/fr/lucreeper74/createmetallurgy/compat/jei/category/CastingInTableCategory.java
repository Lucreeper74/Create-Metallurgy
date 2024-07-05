package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Components;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInTableElement;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingTableRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class CastingInTableCategory extends CreateRecipeCategory<CastingTableRecipe> {
    private final CastingInTableElement castingTable = new CastingInTableElement();

    public CastingInTableCategory(Info<CastingTableRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CastingTableRecipe recipe, IFocusGroup focuses) {

        FluidIngredient fluidIngredient = recipe.getFluidIngredient();
        builder
                .addSlot(RecipeIngredientRole.INPUT, 15, 6)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                .addTooltipCallback(addFluidTooltip(fluidIngredient.getRequiredAmount()));
        builder
                .addSlot(RecipeIngredientRole.INPUT, 15, 26)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredient());

        ProcessingOutput output = recipe.getProcessingOutput();
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
                .setBackground(getRenderedSlot(output), -1, -1)
                .addItemStack(getResultItem(recipe))
                .addTooltipCallback(addStochasticTooltip(output));
    }

    @Override
    public void draw(CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        castingTable.draw(graphics, 48, 27);

        graphics.drawString(Minecraft.getInstance().font, Components.translatable(((float) recipe.getProcessingDuration() / 20.0F) + "s").withStyle(ChatFormatting.GRAY),
                95, 26, 0xffffff);
    }
}