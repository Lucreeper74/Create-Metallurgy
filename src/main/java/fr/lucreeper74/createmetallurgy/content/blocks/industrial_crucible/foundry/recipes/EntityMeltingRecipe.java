package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Recipe;

public class EntityMeltingRecipe extends FoundryRecipe {

    protected EntityIngredient entityIngredient;

    public EntityMeltingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CMRecipeTypes.ENTITY_MELTING, params);
        this.entityIngredient = EntityIngredient.EMPTY;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return false;
    }

    public boolean matches(CrucibleBlockEntity be, Recipe<?> recipe, EntityType<?> type) {
        return match(be, recipe) && entityIngredient.test(type);
    }

    public EntityIngredient getEntityIngredient() {
        return entityIngredient;
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        entityIngredient = EntityIngredient.deserialize(json.getAsJsonObject("entity"));
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);
        json.add("entity", entityIngredient.serialize());
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        entityIngredient = EntityIngredient.read(buffer);
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        entityIngredient.write(buffer);
    }
}