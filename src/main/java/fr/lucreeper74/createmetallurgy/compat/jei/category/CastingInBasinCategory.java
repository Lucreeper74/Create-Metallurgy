package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInBasinElement;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public class CastingInBasinCategory extends CastingAbstractCategory<CastingBasinRecipe> {
    private final CastingInBasinElement castingBasin = new CastingInBasinElement();

    public CastingInBasinCategory(Info<CastingBasinRecipe> info) {
        super(info);
    }

    @Override
    public void draw(CastingBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);

        if (!recipe.getIngredient().isEmpty() && recipe.isMoldConsumed())
            AllIcons.I_DISABLE.render(matrixStack, 14, 44);

        castingBasin.draw(matrixStack, 48, 27);
        drawCastingTime(recipe, matrixStack, 22);
    }
}