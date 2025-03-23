package fr.lucreeper74.createmetallurgy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Pair;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.FoundryData;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class FoundryAbstractCategory<T extends FoundryRecipe> extends CreateRecipeCategory<T> {
    private final AnimatedBlazeBurner burner = new AnimatedBlazeBurner();

    public FoundryAbstractCategory(Info<T> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
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
                    .addSlot(RecipeIngredientRole.INPUT, 8 + xOffset + (i % 3) * 19, 56 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            builder
                    .addSlot(RecipeIngredientRole.INPUT, 8 + xOffset + (i % 3) * 19, 56 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                    .addTooltipCallback(addFluidTooltip(fluidIngredient.getRequiredAmount()));
            i++;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 150 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 56;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(result), -1, -1)
                    .addItemStack(result.getStack())
                    .addTooltipCallback(addStochasticTooltip(result));
            i++;
        }

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 150 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 56;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidResult))
                    .addTooltipCallback(addFluidTooltip(fluidResult.getAmount()));
            i++;
        }
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {

        int minHeat = recipe.getMinHeat();
        boolean noHeating = minHeat < 0;
        int minFoundryWidth = Mth.ceil(Mth.sqrt(Mth.clamp((minHeat / 2), 1, 25)));

        FoundryData.FoundryHeatLevel minHeatLevel = FoundryData.FoundryHeatLevel.getHeatLevel(minHeat, minFoundryWidth * minFoundryWidth);

        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;
        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 144, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeating ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(matrixStack, 81, 58 + (noHeating ? 10 : 30));
        AllGuiTextures heatBar = noHeating ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
        heatBar.render(matrixStack, 4, 80);


        Font font = Minecraft.getInstance().font;
        font.draw(matrixStack, CMLang.translateDirect("foundry." + CMLang.asId(minHeatLevel.name())), 9,
                86, minHeatLevel.getTextColor());

        Component minSizeText = CMLang.translateDirect("recipe.foundry.min_size", minFoundryWidth, minFoundryWidth);
        font.drawShadow(matrixStack, minSizeText, 145, 86, 0xFFFFFF);

        drawProcessTime(recipe, matrixStack, 30);

        if (minHeat >= 0)
            burner.withHeat(getBurnerLevel(minHeatLevel).visualizeAsBlazeBurner())
                    .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);
    }

    public HeatCondition getBurnerLevel(FoundryData.FoundryHeatLevel foundryLevel) {
        return switch (foundryLevel) {
            case COOLING, STABLE -> HeatCondition.NONE;
            case HEATING -> HeatCondition.HEATED;
            case OVERHEATING -> HeatCondition.SUPERHEATED;
        };
    }

    protected void drawProcessTime(T recipe, PoseStack poseStack, int y) {
        int duration = recipe.getProcessingDuration();

        if (duration > 0) {
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", duration / 20f).withStyle(ChatFormatting.GRAY);
            Font renderer = Minecraft.getInstance().font;
            int stringWidth = renderer.width(timeString) + 5;
            renderer.draw(poseStack, timeString, 55 - stringWidth, y, 0xffffff);
        }
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();

        int minXSize = 142;
        int maxXSize = minXSize + 24;
        int minYSize = 82;
        int maxYSize = minYSize + 18;

        if (mouseX >= minXSize && mouseX < maxXSize && mouseY >= minYSize && mouseY < maxYSize)
            tooltip.add(CMLang.translateDirect("recipe.foundry.min_size.text").withStyle(ChatFormatting.RED));

        int minXBurner = 5;
        int maxXBurner = minXBurner + 120 ;
        int minYBurner = 82;
        int maxYBurner = minYBurner + 18;

        if (mouseX >= minXBurner && mouseX < maxXBurner && mouseY >= minYBurner && mouseY < maxYBurner) {
            tooltip.add(CMLang.translateDirect("recipe.foundry.heat_requirement.text"));

            tooltip.add(
                    CMLang.translate("generic.icon.down")
                            .space()
                            .add(CMLang.number(recipe.getMaxHeat()))
                            .space()
                            .translate("generic.unit.thermal")
                            .style(ChatFormatting.RED)
                            .component()
            );

            tooltip.add(
                    CMLang.translate("generic.icon.up")
                            .space()
                            .add(CMLang.number(recipe.getMinHeat()))
                            .space()
                            .translate("generic.unit.thermal")
                            .style(ChatFormatting.GREEN)
                            .component()
            );
        }

        return tooltip;
    }
}
