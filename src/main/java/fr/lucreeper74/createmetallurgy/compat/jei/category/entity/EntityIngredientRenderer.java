package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public record EntityIngredientRenderer(int scale) implements IIngredientRenderer<EntityIngredient.EntityInput> {

    @Override
    public void render(PoseStack matrixStack, @NotNull EntityIngredient.EntityInput entityInput) {
        matrixStack.pushPose();

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level != null) {
            Entity entity = entityInput.type().create(level);

            if (entity instanceof LivingEntity livingEntity) { // No recipes with Non-living entity anyway
                int entityScale = scale;
                float maxSize = entity.getBbHeight() + entity.getBbWidth();
                entityScale /= maxSize;

                PoseStack modelView = RenderSystem.getModelViewStack();
                modelView.pushPose();
                modelView.mulPoseMatrix(matrixStack.last().pose());
                renderEntityInInventory(-15, 25, entityScale, -30, 30, livingEntity);
                modelView.popPose();
                RenderSystem.applyModelViewMatrix();
            }
        }
        matrixStack.popPose();
    }

    @Override
    public List<Component> getTooltip(EntityIngredient.EntityInput entityInput, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityInput.type().getDescription());
        if (tooltipFlag.isAdvanced())
            tooltip.add((Component.literal(Registry.ENTITY_TYPE.getKey(entityInput.type()).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return tooltip;
    }
}