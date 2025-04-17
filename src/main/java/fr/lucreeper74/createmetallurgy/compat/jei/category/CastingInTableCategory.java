package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInTableElement;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingTableRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

public class CastingInTableCategory extends CastingAbstractCategory<CastingTableRecipe> {
    private final CastingInTableElement castingTable = new CastingInTableElement();

    public CastingInTableCategory(Info<CastingTableRecipe> info) {
        super(info);
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
}