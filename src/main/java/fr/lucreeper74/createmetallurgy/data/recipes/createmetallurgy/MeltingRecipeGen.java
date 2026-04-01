package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import java.util.Map;

@SuppressWarnings("unused")
public class MeltingRecipeGen extends CMProcessingRecipesGen {


    GeneratedRecipe

            ALL_METALS = allMetals(),

            CRUSHED_ORES = allCrushed(Map.ofEntries(
                    Map.entry(CMMetals.IRON, AllItems.CRUSHED_IRON),
                    Map.entry(CMMetals.COPPER, AllItems.CRUSHED_COPPER),
                    Map.entry(CMMetals.GOLD, AllItems.CRUSHED_GOLD),
                    Map.entry(CMMetals.ZINC,  AllItems.CRUSHED_ZINC),
                    Map.entry(CMMetals.ALUMINUM, AllItems.CRUSHED_BAUXITE),
                    Map.entry(CMMetals.LEAD, AllItems.CRUSHED_LEAD),
                    Map.entry(CMMetals.NICKEL, AllItems.CRUSHED_NICKEL),
                    Map.entry(CMMetals.OSMIUM, AllItems.CRUSHED_OSMIUM),
                    Map.entry(CMMetals.SILVER, AllItems.CRUSHED_SILVER),
                    Map.entry(CMMetals.TIN, AllItems.CRUSHED_TIN)
                    ))

            ;
    //

    protected GeneratedRecipe allMetals() {
        for (CMMetals metal : CMMetals.values()) {
            for (CMMetals.ItemType type : CMMetals.ItemType.values()) {
                if (type.equals(CMMetals.ItemType.BLOCK)
                        || type.equals(CMMetals.ItemType.RAW_BLOCK)
                        || type.equals(CMMetals.ItemType.RAW_CRUSHED))
                    continue; // Skip blocks that can't be melted using Foundry Basin

                String recipeID = metal.getName() + "/" + type.getName();
                TagKey<Item> inputTag = metal.getItemTag(type);
                int duration = (int) (CMRecipeProvider.MELTING_DURATION * type.getDurationFactor());

                create(recipeID, b -> {
                    b.duration(duration)
                            .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                            .require(inputTag)
                            .requiresHeat(metal.getMeltingPoint() <= HEAT_CONDITION_THRESHOLD ? HeatCondition.HEATED : HeatCondition.SUPERHEATED)
                            .output(metal.getFluid().get(), type.getFluidAmount());

                    if (type.isImpure())
                        b.output(CMFluids.MOLTEN_SLAG.get(), type.getImpurity());

                    return b;
                });
            }
        }
        return null;
    }

    protected GeneratedRecipe allCrushed(Map<CMMetals, ItemEntry<? extends Item>> itemsMap) {
        for (CMMetals metal : itemsMap.keySet()) {
            String recipeID = metal.getName() + "/" + CMMetals.ItemType.RAW_CRUSHED.getName();
            int duration = (int) (CMRecipeProvider.CASTING_DURATION * CMMetals.ItemType.RAW_CRUSHED.getDurationFactor());

            create(recipeID, b -> {
                b.duration(duration)
                        .require(itemsMap.get(metal))
                        .requiresHeat(metal.getMeltingPoint() <= HEAT_CONDITION_THRESHOLD ? HeatCondition.HEATED : HeatCondition.SUPERHEATED)
                        .output(metal.getFluid().get(), CMMetals.ItemType.RAW_CRUSHED.getFluidAmount())
                        .output(CMFluids.MOLTEN_SLAG.get(), CMMetals.ItemType.RAW_CRUSHED.getImpurity());
                return b;
            });
        }
        return null;
    }

    //

    public MeltingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.MELTING;
    }
}
