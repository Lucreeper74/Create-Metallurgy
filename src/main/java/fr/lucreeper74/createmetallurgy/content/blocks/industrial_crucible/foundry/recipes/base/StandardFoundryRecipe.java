package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StandardFoundryRecipe extends FoundryRecipe<FoundryRecipeParams> {

    public StandardFoundryRecipe(CMRecipeTypes type, FoundryRecipeParams params) {
        super(type, params);
    }

    @FunctionalInterface
    public interface Factory<R extends StandardFoundryRecipe> extends FoundryRecipe.Factory<FoundryRecipeParams, R> {
        R create(FoundryRecipeParams params);
    }

    public static class Builder<R extends StandardFoundryRecipe> extends FoundryRecipeBuilder<FoundryRecipeParams, R, StandardFoundryRecipe.Builder<R>> {

        public Builder(Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected FoundryRecipeParams createParams() {
            return new FoundryRecipeParams();
        }

        @Override
        public StandardFoundryRecipe.Builder<R> self() {
            return this;
        }
    }

    public static class Serializer<R extends StandardFoundryRecipe> implements RecipeSerializer<R> {
        private final Factory<R> factory;
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(Factory<R> factory) {
            this.factory = factory;
            this.codec = ProcessingRecipe.codec(factory, FoundryRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, FoundryRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

        public StandardFoundryRecipe.Factory<R> factory() {
            return factory;
        }
    }
}
