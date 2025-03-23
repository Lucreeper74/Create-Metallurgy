package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryElement;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public class BulkMeltingCategory extends FoundryAbstractCategory<BulkMeltingRecipe> {
    private final FoundryElement foundry = new FoundryElement();

    public BulkMeltingCategory(Info<BulkMeltingRecipe> info) {
        super(info);
    }

    @Override
    public void draw(BulkMeltingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        foundry.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    }
}