package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.utils.CategoryHelper;
import fr.lucreeper74.createmetallurgy.compat.jei.category.animations.AnimatedFoundryMixer;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.HEATJS_LOADED;

public class AlloyingCategory extends FoundryBasinCategory {
    private final AnimatedFoundryMixer mixer = new AnimatedFoundryMixer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public AlloyingCategory(Info<FoundryBasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(FoundryBasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);

        mixer.draw(graphics, getBackground().getWidth() / 2 + 3, 34);

        int heater_xOffset = getBackground().getWidth() / 2 + 3;
        int heater_yOffset = 55;
        if (HEATJS_LOADED) {
            if (CategoryHelper.drawCustomHeatSource(graphics, iRecipeSlotsView, recipe,
                    heater_xOffset, heater_yOffset, getBackground().getWidth(), getBackground().getHeight(), mouseX, mouseY))
                return;
        }
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(graphics, heater_xOffset, heater_yOffset);
    }
}