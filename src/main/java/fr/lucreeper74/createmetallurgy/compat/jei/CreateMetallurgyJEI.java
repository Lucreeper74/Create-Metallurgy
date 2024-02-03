package fr.lucreeper74.createmetallurgy.compat.jei;

import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.jei.category.AlloyingCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.CastingInBasinCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.CastingInTableCategory;
import fr.lucreeper74.createmetallurgy.compat.jei.category.MeltingCategory;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableRecipe;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
public class CreateMetallurgyJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CreateMetallurgy.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>
                melting = builder(BasinRecipe.class)
                .addTypedRecipes(AllRecipeTypes.MELTING).catalyst(AllBlocks.FOUNDRY_LID_BLOCK::get)
                .catalyst(AllBlocks.FOUNDRY_BASIN_BLOCK::get)
                .doubleItemIcon(AllBlocks.FOUNDRY_BASIN_BLOCK.get(), AllBlocks.FOUNDRY_LID_BLOCK.get())
                .emptyBackground(177, 100)
                .build("melting", MeltingCategory::new),

                alloying = builder(BasinRecipe.class)
                        .addTypedRecipes(AllRecipeTypes.ALLOYING)
                        .catalyst(AllBlocks.FOUNDRY_MIXER_BLOCK::get)
                        .catalyst(AllBlocks.GLASSED_FOUNDRY_LID_BLOCK::get)
                        .catalyst(AllBlocks.FOUNDRY_BASIN_BLOCK::get)
                        .doubleItemIcon(AllBlocks.FOUNDRY_BASIN_BLOCK.get(), AllBlocks.FOUNDRY_MIXER_BLOCK.get())
                        .emptyBackground(177, 100)
                        .build("alloying", AlloyingCategory::new),

                casting_in_basin = builder(CastingBasinRecipe.class)
                        .addTypedRecipes(AllRecipeTypes.CASTING_IN_BASIN)
                        .catalyst(com.simibubi.create.AllBlocks.SPOUT::get)
                        .catalyst(AllBlocks.CASTING_BASIN_BLOCK::get)
                        .doubleItemIcon(AllBlocks.CASTING_BASIN_BLOCK.get(), Items.CLOCK)
                        .emptyBackground(177, 53)
                        .build("casting_in_basin", CastingInBasinCategory::new),

                casting_in_table = builder(CastingTableRecipe.class)
                        .addTypedRecipes(AllRecipeTypes.CASTING_IN_TABLE)
                        .catalyst(com.simibubi.create.AllBlocks.SPOUT::get)
                        .catalyst(AllBlocks.CASTING_TABLE_BLOCK::get)
                        .doubleItemIcon(AllBlocks.CASTING_TABLE_BLOCK.get(), Items.CLOCK)
                        .emptyBackground(177, 53)
                        .build("casting_in_table", CastingInTableCategory::new);
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        allCategories.forEach(c -> c.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
    }

    //------------------------------------------------------------------------------------------------------------------

    private class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            return addTypedRecipes(recipeTypeEntry::getType);
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(new ResourceLocation(CreateMetallurgy.MOD_ID, name), recipeClass),
                    Component.translatable(CreateMetallurgy.MOD_ID + ".recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            allCategories.add(category);
            return category;
        }
    }
}
