package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import fr.lucreeper74.createmetallurgy.compat.jei.CMJeiConstants;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.FoundryElement;
import fr.lucreeper74.createmetallurgy.compat.jei.category.entity.EntityIngredientRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient.EntityInput;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;

public class EntityMeltingCategory extends FoundryAbstractCategory<EntityMeltingRecipe> {
    private final FoundryElement foundry = new FoundryElement();

    public EntityMeltingCategory(Info<EntityMeltingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EntityMeltingRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);

        EntityIngredient entityIngredient = recipe.getEntityIngredient();
        IIngredientRenderer<EntityInput> renderer = new EntityIngredientRenderer(45);

        boolean hasIngredients = !recipe.getIngredients().isEmpty() || !recipe.getFluidIngredients().isEmpty();

        builder.addSlot(RecipeIngredientRole.INPUT, 50, hasIngredients ? 10 : 30)
                .setCustomRenderer(CMJeiConstants.ENTITY_TYPE, renderer)
                .addIngredients(CMJeiConstants.ENTITY_TYPE, entityIngredient.getDisplay())
                .setBackground(asDrawable(AllGuiTextures.JEI_QUESTION_MARK), 2, 1);

    }

    @Override
    public void draw(EntityMeltingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        boolean hasIngredients = !recipe.getIngredients().isEmpty() || !recipe.getFluidIngredients().isEmpty();
        foundry.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);

        AllGuiTextures.JEI_SHADOW.render(matrixStack, 9, hasIngredients ? 29 : 49);
        Minecraft.getInstance().font.drawShadow(matrixStack, recipe.getEntityIngredient().getDamage() + CMLang.translateDirect("generic.icon.heart").getString(), 48, hasIngredients ? 35 : 55, 0xEE1313);


        if (!recipe.getIngredients().isEmpty() || !recipe.getFluidIngredients().isEmpty())
            AllIcons.I_ADD.render(matrixStack, 27, 40);

    }
}