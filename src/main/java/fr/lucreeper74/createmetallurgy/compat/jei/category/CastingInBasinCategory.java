package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInBasinElement;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

public class CastingInBasinCategory extends CastingAbstractCategory<CastingBasinRecipe> {
    private final CastingInBasinElement castingBasin = new CastingInBasinElement();

    public CastingInBasinCategory(Info<CastingBasinRecipe> info) {
        super(info);
    }

    @Override
    public void draw(CastingBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);

        if (!recipe.getIngredient().isEmpty() && recipe.isMoldConsumed())
            AllIcons.I_DISABLE.render(graphics, 14, 44);

        castingBasin.draw(graphics, 48, 27);
        drawCastingTime(recipe, graphics, 22);
    }
}