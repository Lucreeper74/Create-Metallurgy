package fr.lucreeper74.createmetallurgy.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.jei.category.*;
import fr.lucreeper74.createmetallurgy.compat.jei.category.entity.EntityIngredientHelper;
import fr.lucreeper74.createmetallurgy.compat.jei.category.entity.EntityIngredientRenderer;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.GrindingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingTableRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.MobMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
public class CreateMetallurgyJEI implements IModPlugin {

    private static final ResourceLocation ID = CreateMetallurgy.asResource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }


    private static final List<CreateRecipeCategory<?>> ALL_CATEGORIES = new ArrayList<>();

    private void loadCategories() {
        ALL_CATEGORIES.clear();

        CreateRecipeCategory<?>
                melting = builder(FoundryBasinRecipe.class)
                .addTypedRecipes(CMRecipeTypes.MELTING).catalyst(CMBlocks.FOUNDRY_LID_BLOCK::get)
                .catalyst(CMBlocks.FOUNDRY_BASIN_BLOCK::get)
                .doubleItemIcon(CMBlocks.FOUNDRY_LID_BLOCK.get(), CMBlocks.FOUNDRY_BASIN_BLOCK.get())
                .emptyBackground(177, 100)
                .build("melting", MeltingCategory::new),

                alloying = builder(FoundryBasinRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.ALLOYING)
                        .catalyst(CMBlocks.FOUNDRY_MIXER_BLOCK::get)
                        .catalyst(CMBlocks.FOUNDRY_BASIN_BLOCK::get)
                        .doubleItemIcon(CMBlocks.FOUNDRY_MIXER_BLOCK.get(), CMBlocks.FOUNDRY_BASIN_BLOCK.get())
                        .emptyBackground(177, 100)
                        .build("alloying", AlloyingCategory::new),

                casting_in_basin = builder(CastingBasinRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.CASTING_IN_BASIN)
                        .catalyst(AllBlocks.SPOUT::get)
                        .catalyst(CMBlocks.CASTING_BASIN_BLOCK::get)
                        .doubleItemIcon(CMBlocks.CASTING_BASIN_BLOCK.get(), Items.CLOCK)
                        .emptyBackground(177, 63)
                        .build("casting_in_basin", CastingInBasinCategory::new),

                casting_in_table = builder(CastingTableRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.CASTING_IN_TABLE)
                        .catalyst(AllBlocks.SPOUT::get)
                        .catalyst(CMBlocks.CASTING_TABLE_BLOCK::get)
                        .doubleItemIcon(CMBlocks.CASTING_TABLE_BLOCK.get(), Items.CLOCK)
                        .emptyBackground(177, 63)
                        .build("casting_in_table", CastingInTableCategory::new),

                grinding = builder(GrindingRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.GRINDING)
                        .catalyst(CMBlocks.BELT_GRINDER_BLOCK::get)
                        .doubleItemIcon(CMBlocks.BELT_GRINDER_BLOCK.get(), Items.IRON_INGOT)
                        .emptyBackground(177, 70)
                        .build("grinding", GrindingCategory::new),

                polishing_with_grinder = builder(SandPaperPolishingRecipe.class)
                        .addTypedRecipes(AllRecipeTypes.SANDPAPER_POLISHING)
                        .catalyst(CMBlocks.BELT_GRINDER_BLOCK::get)
                        .doubleItemIcon(CMBlocks.BELT_GRINDER_BLOCK.get(), AllItems.SAND_PAPER.get())
                        .emptyBackground(177, 70)
                        .build("polishing_with_grinder", PolishingWithGrinderCategory::new),

                bulk_melting = builder(BulkMeltingRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.BULK_MELTING)
                        .catalyst(CMBlocks.INDUSTRIAL_CRUCIBLE::get)
                        .catalyst(CMItems.GAUGE_ATTACHMENT::get)
                        .doubleItemIcon(CMBlocks.INDUSTRIAL_CRUCIBLE.get(), Items.BLAZE_POWDER)
                        .emptyBackground(177, 100)
                        .build("bulk_melting", BulkMeltingCategory::new),

                entity_melting = builder(MobMeltingRecipe.class)
                        .addTypedRecipes(CMRecipeTypes.ENTITY_MELTING)
                        .catalyst(CMBlocks.INDUSTRIAL_CRUCIBLE::get)
                        .catalyst(CMItems.GAUGE_ATTACHMENT::get)
                        .doubleItemIcon(CMBlocks.INDUSTRIAL_CRUCIBLE.get(), Items.TROPICAL_FISH)
                        .emptyBackground(177, 100)
                        .build("entity_melting", EntityMeltingCategory::new);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(ALL_CATEGORIES.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        List<DamagedEntityIngredient.EntityStack> entities =
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(entityType -> {
                            Level level = Minecraft.getInstance().level;
                            if (level == null)
                                return false;
                            return entityType.create(level) instanceof LivingEntity;
                        })
                        .map(DamagedEntityIngredient.EntityStack::new)
                        .toList();

        registration.register(CMJeiTypes.ENTITY_STACK, entities, new EntityIngredientHelper(), new EntityIngredientRenderer(false), DamagedEntityIngredient.EntityStack.CODEC);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ALL_CATEGORIES.forEach(c -> c.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ALL_CATEGORIES.forEach(c -> c.registerCatalysts(registration));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private static class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            ALL_CATEGORIES.add(category);
            return category;
        }

        @Override
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            return build(CreateMetallurgy.asResource(name), factory);
        }
    }
}
