package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public record EntityIngredientRenderer(int scale) implements IIngredientRenderer<DamagedEntityIngredient.EntityStack> {

    @Override
    public void render(GuiGraphics graphics, @NotNull DamagedEntityIngredient.EntityStack entityInput) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null)
            return;

        Entity entity = entityInput.type().create(level);

        if (!(entity instanceof LivingEntity living))
            return; // No recipes with Non-living entity anyway

        float size = entity.getBbHeight() + entity.getBbWidth();
        float entityScale = scale / Math.max(1.0f, size);

//                PoseStack modelView = RenderSystem.getModelViewStack();
//                modelView.pushPose();
//                modelView.mulPose(matrixStack.last().pose());
        Quaternionf angle = (new Quaternionf()).rotationXYZ(0, ((float) Math.PI / 180f) * 160f, (float) Math.PI);
        living.setYHeadRot(0);
        renderEntityInInventory(graphics, -15, 25, entityScale, new Vector3f(), angle, null, living);
//                modelView.popPose();
//                RenderSystem.applyModelViewMatrix();

    }

    @Override
    public List<Component> getTooltip(DamagedEntityIngredient.EntityStack entityInput, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityInput.type().getDescription());
        if (tooltipFlag.isAdvanced())
            tooltip.add((Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(entityInput.type()).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return tooltip;
    }
}