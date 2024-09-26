package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.lucreeper74.createmetallurgy.compat.jei.category.animations.AnimatedFoundryMixer;
import fr.lucreeper74.createmetallurgy.content.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public class AlloyingCategory extends FoundryBasinCategory {
    private final AnimatedFoundryMixer mixer = new AnimatedFoundryMixer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public AlloyingCategory(Info<FoundryBasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(FoundryBasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);
        mixer.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    }
}