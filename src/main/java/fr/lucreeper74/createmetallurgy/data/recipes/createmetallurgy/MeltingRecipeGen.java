package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy;

import com.simibubi.create.content.processing.recipe.HeatCondition;
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

@SuppressWarnings("unused")
public class MeltingRecipeGen extends CMProcessingRecipesGen {


    GeneratedRecipe

            ALL_METALS = allMetals();
    //

    protected GeneratedRecipe allMetals() {
        for (CMMetals metal : CMMetals.values()) {
            for (CMMetals.MetalItemType type : CMMetals.MetalItemType.values()) {
                if (type.equals(CMMetals.MetalItemType.BLOCK))
                    continue; // Skip blocks that can't be melted using Foundry Basin

                String metalName = metal.getName();
                String recipeID = metalName + "/" + type.getName();
                TagKey<Item> inputTag = type.getItemTag(metalName);
                int duration = (int) (CMRecipeProvider.MELTING_DURATION * type.getDurationFactor());

                // TODO: Change heat condition to be based on the fluid temp.
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

    //

    public MeltingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.MELTING;
    }
}
