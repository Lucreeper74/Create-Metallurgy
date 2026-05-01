package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.codec.CreateCodecs;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingTableRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

public abstract class CastingRecipeSerializer implements RecipeSerializer<CastingRecipe> {
    private final MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.either(CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE, Ingredient.CODEC).listOf().fieldOf("ingredients")
                            .forGetter(CastingRecipe::ingredients),
                    CastingOutput.CODEC.fieldOf("result")
                            .forGetter(CastingRecipe::getResult),
                    Codec.INT.optionalFieldOf("processing_time", 0)
                            .forGetter(CastingRecipe::getProcessingDuration),
                    Codec.BOOL.optionalFieldOf("mold_consumed", false).forGetter(CastingRecipe::isMoldConsumed)
            ).apply(i, (ingredients, result, duration, moldConsumed) -> {
                CastingRecipe recipe = createRecipe();
                ingredients.forEach(either -> either
                        .ifRight(item -> recipe.ingredient = item)
                        .ifLeft(fluid -> recipe.fluidIngredient = fluid));
                recipe.result = result;
                recipe.processingDuration = duration;
                recipe.moldConsumed = moldConsumed;
                return recipe;
            })
    );

    public final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
            SizedFluidIngredient.STREAM_CODEC, r -> r.fluidIngredient,
            CastingOutput.STREAM_CODEC, r -> r.result,
            ByteBufCodecs.VAR_INT, r -> r.processingDuration,
            ByteBufCodecs.BOOL, r -> r.moldConsumed,
            (ingredient, fluidIngredient, result, duration, moldConsumed) -> {
                CastingRecipe recipe = createRecipe();
                recipe.ingredient = ingredient;
                recipe.fluidIngredient = fluidIngredient;
                recipe.result = result;
                recipe.processingDuration = duration;
                recipe.moldConsumed = moldConsumed;
                return recipe;
            }
    );


    public abstract CastingRecipe createRecipe();

    @Override
    public @NotNull MapCodec<CastingRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static class CastingTableRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe() {
            return new CastingTableRecipe();
        }
    }

    public static class CastingBasinRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe() {
            return new CastingBasinRecipe();
        }
    }
}