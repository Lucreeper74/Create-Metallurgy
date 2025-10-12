package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public record EntityIngredientRenderer(int scale) implements IIngredientRenderer<EntityIngredient.EntityStack> {

    @Override
    public void render(GuiGraphics graphics, @NotNull EntityIngredient.EntityStack entityInput) {
        PoseStack matrixStack = graphics.pose();
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
                Quaternionf angle = (new Quaternionf()).rotationXYZ(0, ((float) Math.PI/180f) * 160f, (float) Math.PI);
                livingEntity.setYHeadRot(0);
                renderEntityInInventory(graphics, -15, 25, entityScale, angle, null, livingEntity);
                modelView.popPose();
                RenderSystem.applyModelViewMatrix();
            }
        }
        matrixStack.popPose();
    }

    @Override
    public List<Component> getTooltip(EntityIngredient.EntityStack entityInput, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityInput.type().getDescription());
        if (tooltipFlag.isAdvanced())
            tooltip.add((Component.literal(ForgeRegistries.ENTITY_TYPES.getKey(entityInput.type()).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return tooltip;
    }
}