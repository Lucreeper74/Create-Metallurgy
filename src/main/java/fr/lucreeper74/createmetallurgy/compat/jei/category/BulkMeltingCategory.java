package fr.lucreeper74.createmetallurgy.compat.jei.category;

import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryElement;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

public class BulkMeltingCategory extends FoundryAbstractCategory<BulkMeltingRecipe> {
    private final FoundryElement foundry = new FoundryElement();

    public BulkMeltingCategory(Info<BulkMeltingRecipe> info) {
        super(info);
    }

    @Override
    public void draw(BulkMeltingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);

        foundry.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
    }
}