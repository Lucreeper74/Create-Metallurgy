package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.casting;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMCastingRecipeGen extends CastingRecipeGen {

    public CMCastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

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

    COPPER_CASING = basinWithMoldTag(Tags.Items.STRIPPED_LOGS, true, CMFluids.MOLTEN_COPPER, 90, AllBlocks.COPPER_CASING.get(), 70),
            BRASS_CASING = basinWithMoldTag(Tags.Items.STRIPPED_LOGS, true, CMFluids.MOLTEN_BRASS, 90, AllBlocks.BRASS_CASING.get(), 70),

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
}