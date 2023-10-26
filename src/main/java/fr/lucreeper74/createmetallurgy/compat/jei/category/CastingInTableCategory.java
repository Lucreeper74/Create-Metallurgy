package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import fr.lucreeper74.createmetallurgy.compat.jei.category.elements.CastingInTableElement;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

public class CastingInTableCategory extends CreateRecipeCategory<CastingTableRecipe> {
        private final CastingInTableElement castingTable = new CastingInTableElement();
        private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

        public CastingInTableCategory(Info<CastingTableRecipe> info) {
            super(info);
        }
    @Override
        public void setRecipe(IRecipeLayoutBuilder builder, CastingTableRecipe recipe, IFocusGroup focuses) {
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
            for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
                builder
                        .addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                        .setBackground(getRenderedSlot(), -1, -1)
                        .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                        .addTooltipCallback(addFluidTooltip(fluidIngredient.getRequiredAmount()));
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
                        .addTooltipCallback(addStochasticTooltip(result));
                i++;
            }

            for (FluidStack fluidResult : recipe.getFluidResults()) {
                int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
                int yPosition = -19 * (i / 2) + 51;

                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(), -1, -1)
                        .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidResult))
                        .addTooltipCallback(addFluidTooltip(fluidResult.getAmount()));
                i++;
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
        public void draw(CastingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
            HeatCondition requiredHeat = recipe.getRequiredHeat();

            boolean noHeat = requiredHeat == HeatCondition.NONE;

            int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

            if (vRows <= 2)
                AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 136, -19 * (vRows - 1) + 32);

            AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
            shadow.render(matrixStack, 81, 58 + (noHeat ? 10 : 30));

            castingTable.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);

            if (requiredHeat != HeatCondition.NONE)
                heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                        .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);

            AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
            heatBar.render(matrixStack, 4, 80);
            Minecraft.getInstance().font.draw(matrixStack, Lang.translateDirect(requiredHeat.getTranslationKey()), 9,
                    86, requiredHeat.getColor());

            Minecraft.getInstance().font.draw(matrixStack, Components.translatable(((float) recipe.getProcessingDuration() / 20.0F) + "s"),
                    getBackground().getWidth() / 2 + 9,
                    30, 0xffffff);
        }
}
