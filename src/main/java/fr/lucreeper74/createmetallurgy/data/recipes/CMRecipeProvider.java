package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.create.*;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.alloying.CMAlloyingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry.CMBulkMeltingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.foundry.CMMobMeltingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.grinding.CMGrindingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.melting.CMMeltingRecipeGen;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CMRecipeProvider extends RecipeProvider {

    protected static final List<ProcessingRecipeGen<?, ?, ?>> GENS = new ArrayList<>();

    protected final List<GeneratedRecipe> all = new ArrayList<>();

    public static final int MELTING_DURATION = 40; // Duration in tick of the melting of an Ingot
    public static final int CASTING_DURATION = 60; // Duration in tick of the casting of an Ingot
    public static final int HEAT_CONDITION_THRESHOLD = 2000; // Threshold after which the condition is superheated

    public CMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        all.forEach(c -> c.register(recipeOutput));
        CreateMetallurgy.LOGGER.info("{} registered {} recipe{}", getName(), all.size(), all.size() == 1 ? "" : "s");
    }

    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENS.add(new CMGrindingRecipeGen(output, registries));
        GENS.add(new CMMeltingRecipeGen(output, registries));
        GENS.add(new CMAlloyingRecipeGen(output, registries));
        GENS.add(new CMBulkMeltingRecipeGen(output, registries));
        GENS.add(new CMMobMeltingRecipeGen(output, registries));

        /* Create Recipes */
        GENS.add(new CMMixingRecipeGen(output, registries));
        GENS.add(new CMCrushingRecipeGen(output, registries));
        GENS.add(new CMMillingRecipeGen(output, registries));
        GENS.add(new CMWashingRecipeGen(output, registries));
        GENS.add(new CMPressingRecipeGen(output, registries));
        GENS.add(new CMCompactingRecipeGen(output, registries));

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


    protected GeneratedRecipe register(GeneratedRecipe recipe) {
        all.add(recipe);
        return recipe;
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(RecipeOutput recipeOutput);
    }

    // Shortcut for tags & items
    public static class T {

        /* Vanilla tags / Items */
        public static ItemLike coal() {
            return Items.COAL;
        }

        /* Create tags / Items */
        public static TagKey<Item> sandpaper() {
            return AllTags.AllItemTags.SANDPAPER.tag;
        }

        public static TagKey<Item> sleepers() {
            return AllTags.AllItemTags.SLEEPERS.tag;
        }

        public static ItemLike andesiteAlloy() {
            return AllItems.ANDESITE_ALLOY.get();
        }

        public static ItemLike andesiteCasing() {
            return AllBlocks.ANDESITE_CASING.get();
        }

        public static ItemLike copperCasing() {
            return AllBlocks.COPPER_CASING.get();
        }

        public static ItemLike shaft() {
            return AllBlocks.SHAFT.get();
        }

        public static ItemLike cog() {
            return AllBlocks.COGWHEEL.get();
        }

        /* Create Metallurgy tags / Items */
        public static ItemLike sandpaperBelt() {
            return CMItems.SANDPAPER_BELT.get();
        }

        public static ItemLike refractoryMortar() {
            return CMBlocks.REFRACTORY_MORTAR.get();
        }

        public static ItemLike refractoryMortarBall() {
            return CMItems.REFRACTORY_MORTAR_BALL.get();
        }

        public static TagKey<Item> coke() {
            return CMItemTags.COAL_COKE.tag;
        }

        public static TagKey<Item> tungstenIngot() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.INGOT);
        }

        public static TagKey<Item> tungstenSheet() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.PLATE);
        }

        public static TagKey<Item> tungstenWire() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.WIRE);
        }

        public static TagKey<Item> rawWolframite() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_MATERIAL);
        }

        public static TagKey<Item> wolframiteBlock() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_BLOCK);
        }

        public static TagKey<Item> obduriumIngot() {
            return CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.INGOT);
        }

        public static TagKey<Item> obduriumSheet() {
            return CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.PLATE);
        }

        public static TagKey<Item> steelIngot() {
            return CMMetals.STEEL.getItemTag(CMMetals.ItemType.INGOT);
        }
    }
}
