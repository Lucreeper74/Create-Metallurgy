package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.create.*;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.AlloyingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.GrindingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.MeltingRecipeGen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class CMProcessingRecipesGen extends CMRecipeProvider {
    protected static final List<CMProcessingRecipesGen> GENS = new ArrayList<>();

    public static void registerAll(DataGenerator gen, PackOutput output) {
        GENS.add(new GrindingRecipeGen(output));
        GENS.add(new MeltingRecipeGen(output));
        GENS.add(new AlloyingRecipeGen(output));

        /* Create Recipes */
        GENS.add(new CMMixingRecipeGen(output));
        GENS.add(new CMCrushingRecipeGen(output));
        GENS.add(new CMMillingRecipeGen(output));
        GENS.add(new CMWashingRecipeGen(output));
        GENS.add(new CMPressingRecipeGen(output));
        GENS.add(new CMCompactingRecipeGen(output));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Create: Metallurgy's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

    public CMProcessingRecipesGen(PackOutput output) {
        super(output);
    }


    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient,
                                                                                UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return create(CreateMetallurgy.MOD_ID, singleIngredient, transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String name,
                                                                     UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return create(CreateMetallurgy.genRL(name), transform);
    }


    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(ResourceLocation name,
                                                                     UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return createWithDeferredId(() -> name, transform);
    }

    /**
     * Recipe with recipe name provided by the function
     */
    protected <T extends ProcessingRecipe<?>> GeneratedRecipe createWithDeferredId(Supplier<ResourceLocation> name,
                                                                                   UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        ProcessingRecipeSerializer<T> serializer = getSerializer();
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new ProcessingRecipeBuilder<>(serializer.getFactory(), name.get()))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }


    /**
     * Recipe with single ingredient (its name as recipe name)
     */
    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String namespace,
                                                                     Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        ProcessingRecipeSerializer<T> serializer = getSerializer();
        GeneratedRecipe generatedRecipe = c -> {
            ItemLike itemLike = singleIngredient.get();
            transform
                    .apply(new ProcessingRecipeBuilder<>(serializer.getFactory(),
                            new ResourceLocation(namespace, CatnipServices.REGISTRIES.getKeyOrThrow(itemLike.asItem())
                                    .getPath())).withItemIngredients(Ingredient.of(itemLike)))
                    .build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected abstract IRecipeTypeInfo getRecipeType();

    protected <T extends ProcessingRecipe<?>> ProcessingRecipeSerializer<T> getSerializer() {
        return getRecipeType().getSerializer();
    }
}