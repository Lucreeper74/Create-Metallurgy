package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.CMCompatMetals;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import java.util.function.UnaryOperator;

import static com.simibubi.create.AllTags.forgeItemTag;

@SuppressWarnings("unused")
public class CastingRecipeGen extends CreateRecipeProvider {

    GeneratedRecipe

            COMPAT_METALS = moddedMetals(),

            IRON_METAL = standardMetals(CMFluids.MOLTEN_IRON, Items.IRON_BLOCK, Items.IRON_INGOT, AllItems.IRON_SHEET.get(), Items.IRON_NUGGET, "iron"),
                GOLD_METAL = standardMetals(CMFluids.MOLTEN_GOLD, Items.GOLD_BLOCK, Items.GOLD_INGOT, AllItems.GOLDEN_SHEET.get(), Items.GOLD_NUGGET, "gold"),
                COPPER_METAL = standardMetals(CMFluids.MOLTEN_COPPER, Items.COPPER_BLOCK, Items.COPPER_INGOT, AllItems.COPPER_SHEET.get(), AllItems.COPPER_NUGGET.get(), "copper"),
                BRASS_METAL = standardMetals(CMFluids.MOLTEN_BRASS, AllBlocks.BRASS_BLOCK.get(), AllItems.BRASS_INGOT.get(), AllItems.BRASS_SHEET.get(), AllItems.BRASS_NUGGET.get(), "brass"),
                ZINC_METAL = standardMetals(CMFluids.MOLTEN_ZINC, AllBlocks.ZINC_BLOCK.get(), AllItems.ZINC_INGOT.get(), null, AllItems.ZINC_NUGGET.get(), "zinc"),
                STEEL_METAL = standardMetals(CMFluids.MOLTEN_STEEL, CMBlocks.STEEL_BLOCK.get(), CMItems.STEEL_INGOT.get(), null, null, "steel"),
                TUNGSTEN_METAL = standardMetals(CMFluids.MOLTEN_TUNGSTEN, CMBlocks.TUNGSTEN_BLOCK.get(), CMItems.TUNGSTEN_INGOT.get(), CMItems.TUNGSTEN_SHEET.get(), CMItems.TUNGSTEN_NUGGET.get(), "tungsten"),
                NETHERITE_METAL = standardMetals(CMFluids.MOLTEN_NETHERITE, Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, null, null, "netherite"),

            ANDESITE_ALLOY_FROM_IRON = basin("andesite_alloy_from_iron", Items.ANDESITE, true, CMFluids.MOLTEN_IRON, 90, AllBlocks.ANDESITE_ALLOY_BLOCK.get(), 360),
                ANDESITE_ALLOY_FROM_ZINC = basin("andesite_alloy_from_zinc", Items.ANDESITE, true, CMFluids.MOLTEN_ZINC, 90, AllBlocks.ANDESITE_ALLOY_BLOCK.get(), 360),

//          ANDESITE_CASING = basinWithMoldTag(AllTags.AllItemTags.STRIPPED_LOGS.tag, CMFluids.MOLTEN_ANDESITE_ALLOY, 90, AllBlocks.ANDESITE_CASING, 200),
                COPPER_CASING = basinWithMoldTag(AllTags.AllItemTags.STRIPPED_LOGS.tag, true, CMFluids.MOLTEN_COPPER, 90, AllBlocks.COPPER_CASING.get(), 70),
                BRASS_CASING = basinWithMoldTag(AllTags.AllItemTags.STRIPPED_LOGS.tag, true, CMFluids.MOLTEN_BRASS, 90, AllBlocks.BRASS_CASING.get(), 70)

    ;

    //

    protected GeneratedRecipe standardMetals(FluidEntry<ForgeFlowingFluid.Flowing> fluid, ItemLike block, ItemLike ingot, ItemLike plate, ItemLike nugget, String metalName) {
        //Items
        table(metalName + "/ingot", CMItems.GRAPHITE_INGOT_MOLD.get(), false, fluid, 90, ingot, 60);

        if (nugget != null)
            table(metalName + "/nugget", CMItems.GRAPHITE_NUGGET_MOLD.get(), false, fluid, 10, nugget, 10);
        else
            tableTag(metalName + "/nugget", CMItems.GRAPHITE_NUGGET_MOLD.get(), false, fluid, 10, forgeItemTag("nuggets/" + metalName), 10);

        if (plate != null)
            table(metalName + "/plate", CMItems.GRAPHITE_PLATE_MOLD.get(), false, fluid, 90, plate, 60);
        else
            tableTag(metalName + "/plate", CMItems.GRAPHITE_PLATE_MOLD.get(), false, fluid, 90, forgeItemTag("plates/" + metalName), 60);

        tableTag(metalName + "/rod", CMItems.GRAPHITE_ROD_MOLD.get(), false, fluid, 45, forgeItemTag("rods/" + metalName), 30);
        tableTag(metalName + "/gear", CMItems.GRAPHITE_GEAR_MOLD.get(), false, fluid, 360, forgeItemTag("gears/" + metalName), 160);
        //Block
        basin(metalName + "/block", fluid, 810, block, 320);
        return null;
    }


    protected GeneratedRecipe moddedMetals() {
        for (CMCompatMetals metal : CMCompatMetals.values()) {
            String metalName = metal.getName();
            //Items
            tableTag(metalName + "/ingot", CMItems.GRAPHITE_INGOT_MOLD.get(), false, metal.getFluid(), 90, forgeItemTag("ingots/" + metalName), 60);
            tableTag(metalName + "/nugget", CMItems.GRAPHITE_NUGGET_MOLD.get(), false, metal.getFluid(), 10, forgeItemTag("nuggets/" + metalName), 10);
            tableTag(metalName + "/plate", CMItems.GRAPHITE_PLATE_MOLD.get(), false, metal.getFluid(), 90, forgeItemTag("plates/" + metalName), 60);
            tableTag(metalName + "/rod", CMItems.GRAPHITE_ROD_MOLD.get(), false, metal.getFluid(), 45, forgeItemTag("rods/" + metalName), 30);
            tableTag(metalName + "/gear", CMItems.GRAPHITE_GEAR_MOLD.get(), false, metal.getFluid(), 360, forgeItemTag("gears/" + metalName), 160);
            //Block
            basinTag(metalName + "/block", metal.getFluid(), 810, forgeItemTag("storage_blocks/" + metalName), 320);
        }
        return null;
    }

    /**
     * Recipes with ouput Tags :
     *
     * @param recipeId  Recipe name / folders
     * @param mold      Mold used (Optional)
     * @param fluid     Input
     * @param amount    Fluid amount
     * @param resultTag Output from tag
     * @param duration  Processing time
     */
    protected GeneratedRecipe tableTag(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTagWithMold(CMRecipeTypes.CASTING_IN_TABLE, recipeId, mold, moldConsumed, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe tableTag(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTag(CMRecipeTypes.CASTING_IN_TABLE, recipeId, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe basinTag(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTagWithMold(CMRecipeTypes.CASTING_IN_BASIN, recipeId, mold, moldConsumed, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe basinTag(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        return castingTag(CMRecipeTypes.CASTING_IN_BASIN, recipeId, fluid, amount, resultTag, duration);
    }

    protected GeneratedRecipe castingTagWithMold(CMRecipeTypes recipeType, String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        ResourceLocation location = resultTag.location();
        create(recipeType, recipeId, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(mold)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(resultTag));

        return null;
    }

    protected GeneratedRecipe castingTag(CMRecipeTypes recipeType, String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> resultTag, int duration) {
        ResourceLocation location = resultTag.location();
        create(recipeType, recipeId, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(fluid.get(), amount)
                .output(resultTag));

        return null;
    }


    /**
     * Recipes with output Items :
     *
     * @param mold     Mold used (Optional)
     * @param fluid    Input
     * @param amount   Fluid amount
     * @param result   Output from Item
     * @param duration Processing time
     */
    protected GeneratedRecipe table(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return castingWithMold(CMRecipeTypes.CASTING_IN_TABLE, recipeId, mold, moldConsumed, fluid, amount, result, duration);
    }

    protected GeneratedRecipe table(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return casting(CMRecipeTypes.CASTING_IN_TABLE, recipeId, fluid, amount, result, duration);
    }

    protected GeneratedRecipe basin(String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return castingWithMold(CMRecipeTypes.CASTING_IN_BASIN, recipeId, mold, moldConsumed, fluid, amount, result, duration);
    }

    protected GeneratedRecipe basin(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        return casting(CMRecipeTypes.CASTING_IN_BASIN, recipeId, fluid, amount, result, duration);
    }

    protected GeneratedRecipe castingWithMold(CMRecipeTypes recipeType, String recipeId, ItemLike mold, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        create(recipeType, recipeId, b -> b.duration(duration)
                .require(mold)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }

    protected GeneratedRecipe casting(CMRecipeTypes recipeType, String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        create(recipeType, recipeId, b -> b.duration(duration)
                .require(fluid.get(), amount)
                .output(result));

        return null;
    }


    /**
     * Recipes with mold Tag :
     *
     * @param moldTag  Mold used (with Tag)
     * @param fluid    Input
     * @param amount   Fluid amount
     * @param result   Output from Item
     * @param duration Processing time
     */

    protected GeneratedRecipe tableWithMoldTag(TagKey<Item> moldTag, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        ResourceLocation location = moldTag.location();
        create(CMRecipeTypes.CASTING_IN_TABLE, result, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(location)))
                .require(moldTag)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }

    protected GeneratedRecipe basinWithMoldTag(TagKey<Item> moldTag, boolean moldConsumed, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, ItemLike result, int duration) {
        ResourceLocation location = moldTag.location();
        create(CMRecipeTypes.CASTING_IN_BASIN, result, b -> b.duration(duration)
                .require(moldTag)
                .require(fluid.get(), amount)
                .withMoldConsumed(moldConsumed)
                .output(result));

        return null;
    }

    //

    public CastingRecipeGen(PackOutput generator) {
        super(generator);
    }

    /**
     * Recipe with result as recipe name
     */
    protected GeneratedRecipe create(CMRecipeTypes type, ItemLike result, UnaryOperator<CastingRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.genRL(RegisteredObjects.getKeyOrThrow(result
                                .asItem()).getPath())))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    /**
     * Recipe with recipe name provided by the function
     */
    protected GeneratedRecipe create(CMRecipeTypes type, String name, UnaryOperator<CastingRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.genRL(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return "Create: Metallurgy's Casting Recipes";
    }
}