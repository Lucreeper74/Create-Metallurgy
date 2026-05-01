package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MobMeltingRecipe extends FoundryRecipe<MobMeltingRecipeParams> {

    private final DamagedEntityIngredient entityIngredient;

    public MobMeltingRecipe(MobMeltingRecipeParams params) {
        super(CMRecipeTypes.ENTITY_MELTING, params);

        entityIngredient = params.entityIngredient;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return false;
    }

    public boolean matches(CrucibleBlockEntity be, EntityType<?> type) {
        return match(be, this) && entityIngredient.test(type) && FoundryRecipe.matchHeatCondition(be, this);
    }

    public DamagedEntityIngredient getEntityIngredient() {
        return entityIngredient;
    }

    @FunctionalInterface
    public interface Factory<R extends MobMeltingRecipe> extends FoundryRecipe.Factory<MobMeltingRecipeParams, R> {
        R create(MobMeltingRecipeParams params);
    }

    public static class Builder<R extends MobMeltingRecipe> extends FoundryRecipeBuilder<MobMeltingRecipeParams, R, Builder<R>> {
        public Builder(Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected MobMeltingRecipeParams createParams() {
            return new MobMeltingRecipeParams();
        }

        @Override
        public MobMeltingRecipe.Builder<R> self() {
            return this;
        }

        public MobMeltingRecipe.Builder<R> requireEntityTag(TagKey<EntityType<?>> tag, int damage) {
            params.entityIngredient = DamagedEntityIngredient.fromTag(tag, damage);
            return self();
        }

        public MobMeltingRecipe.Builder<R> requireEntityType(EntityType<?> type, int damage) {
            params.entityIngredient = DamagedEntityIngredient.fromType(type, damage);
            return self();
        }

        public MobMeltingRecipe.Builder<R> requireEntity(Entity entity, int damage) {
            params.entityIngredient = DamagedEntityIngredient.fromEntity(entity, damage);
            return self();
        }
    }

    public static class Serializer<R extends FoundryRecipe<MobMeltingRecipeParams>> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<MobMeltingRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, MobMeltingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, MobMeltingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

    }
}