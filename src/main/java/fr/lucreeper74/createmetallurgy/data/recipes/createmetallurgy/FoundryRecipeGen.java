package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.CMCompatMetals;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipeBuilder;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.UnaryOperator;

import static com.simibubi.create.AllTags.forgeItemTag;

@SuppressWarnings("unused")
public class FoundryRecipeGen extends CMRecipeProvider {

    GeneratedRecipe

    /* Bulk Melting Recipes */
    COMPAT_METALS_BLOCKS = moddedMetals(),

    IRON_METAL = standardMetals(CMFluids.MOLTEN_IRON, "iron", 4),
            GOLD_METAL = standardMetals(CMFluids.MOLTEN_GOLD, "gold", 4),
            COPPER_METAL = standardMetals(CMFluids.MOLTEN_COPPER, "copper", 4),
            BRASS_METAL = standardMetals(CMFluids.MOLTEN_BRASS, "brass", 4),
            ZINC_METAL = standardMetals(CMFluids.MOLTEN_ZINC, "zinc", 4),
            TUNGSTEN_METAL = standardMetals(CMFluids.MOLTEN_TUNGSTEN, "tungsten", 4),
            OBDURIUM_METAL = standardMetals(CMFluids.MOLTEN_OBDURIUM, "obdurium", 4),
            STEEL_METAL = standardMetals(CMFluids.MOLTEN_STEEL, "steel", 4),
            NETHERITE_METAL = standardMetals(CMFluids.MOLTEN_NETHERITE, "netherite", 16),

    /* Entity Melting Recipes */
    TEST2 = meltingEntity("tadpole",EntityType.TADPOLE, 2,CMFluids.MOLTEN_BRASS, 100, 4);

    protected GeneratedRecipe moddedMetals() {
        for (CMCompatMetals metal : CMCompatMetals.values()) {
            String metalName = metal.getName();
            //Blocks
            meltingTag(metalName + "/block", forgeItemTag("storage_blocks/" + metalName), metal.getFluid(), 810, 4, 200);
        }
        return null;
    }

    protected GeneratedRecipe standardMetals(FluidEntry<ForgeFlowingFluid.Flowing> fluid, String metalName, int minHeat) {
        return meltingTag(metalName + "/block", forgeItemTag("storage_blocks/" + metalName), fluid, 810, minHeat, 200);
    }

    //

    /**
     * Recipes with input Tag :
     *
     * @param recipeId Recipe name / folders
     * @param inputTag Item tag input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingTag(String recipeId, TagKey<Item> inputTag, FluidEntry<ForgeFlowingFluid.Flowing> result, int amount, int minHeat, int duration) {
        return create(recipeId, CMRecipeTypes.BULK_MELTING.getSerializer(), b -> (FoundryRecipeBuilder<FoundryRecipe>) b
                .requiresMinHeat(minHeat)
                .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                .require(inputTag)
                .duration(duration)
                .output(result.get(), amount));
    }

    /**
     * Recipes with input Items :
     *
     * @param recipeId Recipe name / folders
     * @param input    Item input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingItem(String recipeId, ItemLike input, FluidEntry<ForgeFlowingFluid.Flowing> result, int amount, int minHeat, int duration) {
        return create(recipeId, CMRecipeTypes.BULK_MELTING.getSerializer(), b -> (FoundryRecipeBuilder<FoundryRecipe>) b
                .requiresMinHeat(minHeat)
                .require(input)
                .duration(duration)
                .output(result.get(), amount));
    }

    /**
     * Recipes with input EntityType :
     *
     * @param recipeId   Recipe name / folders$
     * @param entityType EntityType input
     * @param damage     Damage to the entity input
     * @param result     Fluid result
     * @param amount     Fluid amount
     * @param minHeat    Minimum Heat condition
     */
    protected GeneratedRecipe meltingEntity(String recipeId, EntityType<?> entityType, int damage, FluidEntry<ForgeFlowingFluid.Flowing> result, int amount, int minHeat) {
        return createEntity(recipeId, b -> (EntityMeltingRecipeBuilder) b
                .requireEntity(entityType, damage)
                .requiresMinHeat(minHeat)
                .output(result.get(), amount));
    }

    //

    public FoundryRecipeGen(DataGenerator generator) {
        super(generator);
    }

    protected <T extends FoundryRecipe> GeneratedRecipe create(String name, ProcessingRecipeSerializer<T> serializer, UnaryOperator<FoundryRecipeBuilder<T>> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new FoundryRecipeBuilder<>(serializer.getFactory(), CreateMetallurgy.genRL(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected GeneratedRecipe createEntity(String name, UnaryOperator<EntityMeltingRecipeBuilder> transform) {
        ProcessingRecipeSerializer<EntityMeltingRecipe> serializer = CMRecipeTypes.ENTITY_MELTING.getSerializer();
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new EntityMeltingRecipeBuilder(serializer.getFactory(), CreateMetallurgy.genRL(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return "Create: Metallurgy's Foundry Recipes";
    }
}