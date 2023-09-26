package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.lucreeper74.createmetallurgy.compat.jei.category.animations.AnimatedFoundryMixer;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public class AlloyingCategory extends BasinCategory {
    private final AnimatedFoundryMixer mixer = new AnimatedFoundryMixer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public AlloyingCategory(Info<BasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);
        mixer.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    }
}