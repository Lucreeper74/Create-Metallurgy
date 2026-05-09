package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.xiaohunao.create_heat_js.common.utils.CategoryHelper;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.HEATJS_LOADED;

public class FoundryBasinCategory extends CreateRecipeCategory<FoundryBasinRecipe> {

    private final boolean needsHeating;

    public FoundryBasinCategory(Info<FoundryBasinRecipe> info, boolean needsHeating) {
        super(info);
        this.needsHeating = needsHeating;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FoundryBasinRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

        int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
        int i = 0;

        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }

            builder
                    .addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            int x = 17 + xOffset + (i % 3) * 19;
            int y = 51 - (i / 3) * 19;
            addFluidSlot(builder, x, y, fluidIngredient);
            i++;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(result), -1, -1)
                    .addItemStack(result.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(result));
            i++;
        }

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int x = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int y = -19 * (i / 2) + 51;

            addFluidSlot(builder, x, y, fluidResult);
            i++;
        }

        if (HEATJS_LOADED) {
            if (CategoryHelper.setCustomHeatSourceRecipe(builder, recipe))
                return;
        }

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (!requiredHeat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.NONE)) {
            builder
                    .addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                    .addItemStack(AllBlocks.BLAZE_BURNER.asStack());
        }
        if (!requiredHeat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.KINDLED)) {
            builder
                    .addSlot(RecipeIngredientRole.CATALYST, 153, 81)
                    .addItemStack(AllItems.BLAZE_CAKE.asStack());
        }
    }

    @Override
    public void draw(FoundryBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();

        boolean noHeat = requiredHeat == HeatCondition.NONE;

        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(graphics, 81, 58 + (noHeat ? 10 : 30));

        if (HEATJS_LOADED) {
            CategoryHelper.drawCustomHeatSource(graphics, recipeSlotsView, recipe,
                    background.getWidth() / 2 + 3, 55, background.getWidth(), background.getHeight(), mouseX, mouseY);
        }

        if (!needsHeating)
            return;

        AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
        heatBar.render(graphics, 4, 80);
        graphics.drawString(Minecraft.getInstance().font, CreateLang.translateDirect(requiredHeat.getTranslationKey()), 9,
                86, requiredHeat.getColor(), false);
    }
}