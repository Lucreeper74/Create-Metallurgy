package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Map;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class CastingRecipeGen extends CMRecipeProvider {

    GeneratedRecipe

            ALL_COMPAT_METALS = allCompatMetals(),

    IRON = allStandard(CMMetals.IRON, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, Items.IRON_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, AllItems.IRON_SHEET),
            Map.entry(CMMetals.ItemType.NUGGET, Items.IRON_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  Items.IRON_BLOCK)
    )),

    COPPER = allStandard(CMMetals.COPPER, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, Items.COPPER_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, AllItems.COPPER_SHEET),
            Map.entry(CMMetals.ItemType.NUGGET, AllItems.COPPER_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  Items.COPPER_BLOCK)
    )),

    GOLD = allStandard(CMMetals.GOLD, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, Items.GOLD_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, AllItems.GOLDEN_SHEET),
            Map.entry(CMMetals.ItemType.NUGGET, Items.GOLD_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  Items.GOLD_BLOCK)
    )),

    NETHERITE = allStandard(CMMetals.NETHERITE, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, Items.NETHERITE_INGOT),
            Map.entry(CMMetals.ItemType.BLOCK,  Items.NETHERITE_BLOCK)
    )),

    ZINC = allStandard(CMMetals.ZINC, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, AllItems.ZINC_INGOT),
            Map.entry(CMMetals.ItemType.NUGGET, AllItems.ZINC_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  AllBlocks.ZINC_BLOCK)
    )),

    BRASS = allStandard(CMMetals.BRASS, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, AllItems.BRASS_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, AllItems.BRASS_SHEET),
            Map.entry(CMMetals.ItemType.NUGGET, AllItems.BRASS_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  AllBlocks.BRASS_BLOCK)
    )),

    TUNGSTEN = allStandard(CMMetals.TUNGSTEN, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, CMItems.TUNGSTEN_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, CMItems.TUNGSTEN_SHEET),
            Map.entry(CMMetals.ItemType.NUGGET, CMItems.TUNGSTEN_NUGGET),
            Map.entry(CMMetals.ItemType.BLOCK,  CMBlocks.TUNGSTEN_BLOCK)
    )),

    OBDURIUM = allStandard(CMMetals.OBDURIUM, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, CMItems.OBDURIUM_INGOT),
            Map.entry(CMMetals.ItemType.PLATE, CMItems.OBDURIUM_SHEET),
            Map.entry(CMMetals.ItemType.BLOCK,  CMBlocks.OBDURIUM_BLOCK)
    )),

    STEEL = allStandard(CMMetals.STEEL, Map.ofEntries(
            Map.entry(CMMetals.ItemType.INGOT, CMItems.STEEL_INGOT),
            Map.entry(CMMetals.ItemType.BLOCK,  CMBlocks.STEEL_BLOCK)
    )),



    ANDESITE_ALLOY_FROM_IRON = basin("andesite_alloy_from_iron", Items.ANDESITE, true, CMFluids.MOLTEN_IRON, 90, AllBlocks.ANDESITE_ALLOY_BLOCK.get(), 360),
            ANDESITE_ALLOY_FROM_ZINC = basin("andesite_alloy_from_zinc", Items.ANDESITE, true, CMFluids.MOLTEN_ZINC, 90, AllBlocks.ANDESITE_ALLOY_BLOCK.get(), 360),

    COPPER_CASING = basinWithMoldTag(AllTags.AllItemTags.STRIPPED_LOGS.tag, true, CMFluids.MOLTEN_COPPER, 90, AllBlocks.COPPER_CASING.get(), 70),
            BRASS_CASING = basinWithMoldTag(AllTags.AllItemTags.STRIPPED_LOGS.tag, true, CMFluids.MOLTEN_BRASS, 90, AllBlocks.BRASS_CASING.get(), 70),

    SLAG = table("slag_casting", CMFluids.MOLTEN_SLAG, 90, CMItems.SLAG.get(), 60);

    //

    protected GeneratedRecipe allCompatMetals() {
        for (CMMetals metal : CMMetals.values()) {
            if (metal.isStandard())
                continue; // Skip all standard metals

            for (CMMetals.ItemType type : CMMetals.ItemType.values()) {
                if (!type.canBeCast())
                    continue; // Skip non-castable items

                CMRecipeTypes recipeType = type.equals(CMMetals.ItemType.BLOCK) ? CMRecipeTypes.CASTING_IN_BASIN : CMRecipeTypes.CASTING_IN_TABLE;

                String recipeID = metal.getName() + "/" + type.getName();
                TagKey<Item> inputTag = metal.getItemTag(type);
                ItemLike requiredItem = type.getItem(inputTag);
                int duration = (int) (CMRecipeProvider.CASTING_DURATION * type.getDurationFactor());

                create(recipeType, recipeID, b -> {
                    b.duration(duration)
                            .require(metal.getFluid().get(), type.getFluidAmount());

                    if (type.hasMold())
                        b.require(type.getMold());

                    b.withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                            .output(inputTag);

                    return b;
                });
            }
        }
        return null;
    }

    protected GeneratedRecipe allStandard(CMMetals metal, Map<CMMetals.ItemType, ItemLike> typesItems) {
            for (CMMetals.ItemType type : CMMetals.ItemType.values()) {
                if (!type.canBeCast())
                    continue; // Skip non-castable items

                CMRecipeTypes recipeType = type.equals(CMMetals.ItemType.BLOCK) ? CMRecipeTypes.CASTING_IN_BASIN : CMRecipeTypes.CASTING_IN_TABLE;

                String recipeID = metal.getName() + "/" + type.getName();
                TagKey<Item> inputTag = metal.getItemTag(type);
                int duration = (int) (CMRecipeProvider.CASTING_DURATION * type.getDurationFactor());

                create(recipeType, recipeID, b -> {
                    b.duration(duration)
                            .require(metal.getFluid().get(), type.getFluidAmount());

                    if (type.hasMold())
                        b.require(type.getMold());

                    if (typesItems.containsKey(type))
                        b.output(typesItems.get(type));
                    else
                        b.withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                                .output(inputTag);

                    return b;
                });
            }
        return null;
    }

    /**
     * Recipes with output Tags :
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
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.genRL(CatnipServices.REGISTRIES.getKeyOrThrow(result
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