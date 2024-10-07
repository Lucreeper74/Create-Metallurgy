package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInTableElement;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingTableRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CastingInTableCategory extends CastingAbstractCategory<CastingTableRecipe> {
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

    @Override
    public void draw(CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

        if (!recipe.getIngredient().isEmpty() && recipe.isMoldConsumed())
            AllIcons.I_DISABLE.render(graphics, 14, 44);

        castingTable.draw(graphics, 48, 27);
        drawCastingTime(recipe, graphics, 22);
    }

    @Override
    @NotNull
    public List<Component> getTooltipStrings(CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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