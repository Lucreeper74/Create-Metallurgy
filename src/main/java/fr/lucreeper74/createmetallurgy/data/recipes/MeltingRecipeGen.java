package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.compat.CMCompatMetals;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Objects;
import java.util.function.Supplier;

import static com.simibubi.create.AllTags.forgeItemTag;

@SuppressWarnings("unused")
public class MeltingRecipeGen extends CMProcessingRecipesGen {


    GeneratedRecipe

            COMPAT_METALS = moddedMetals(),

            IRON_METAL = standardMetals(CMFluids.MOLTEN_IRON, "iron"),
                    GOLD_METAL = standardMetals(CMFluids.MOLTEN_GOLD, "gold"),
                    COPPER_METAL = standardMetals(CMFluids.MOLTEN_COPPER, "copper"),
                    BRASS_METAL = standardMetals(CMFluids.MOLTEN_BRASS, "brass"),
                    ZINC_METAL = standardMetals(CMFluids.MOLTEN_ZINC, "zinc"),
                    TUNGSTEN_METAL = standardMetals(CMFluids.MOLTEN_TUNGSTEN, "tungsten"),
                    STEEL_METAL = standardMetals(CMFluids.MOLTEN_STEEL, "steel"),
                    NETHERITE_METAL = standardMetals(CMFluids.MOLTEN_NETHERITE, "netherite")

            ;

    //

    protected GeneratedRecipe standardMetals(FluidEntry<ForgeFlowingFluid.Flowing> fluid, String metalName) {
        meltingTag(metalName + "/ingot", forgeItemTag("ingots/" + metalName), fluid, 90, HeatCondition.HEATED, 40);
        meltingTag(metalName + "/nugget", forgeItemTag("nuggets/" + metalName), fluid, 10, HeatCondition.HEATED, 4);
        meltingTag(metalName + "/plate", forgeItemTag("plates/" + metalName), fluid, 90, HeatCondition.HEATED, 40);

        if(metalName.equals("tungsten")) {
            meltingTag("wolframite/dirty_dust", forgeItemTag("dirty_dusts/wolframite"), fluid, 90, HeatCondition.HEATED, 30);
            meltingTag("wolframite/dust", forgeItemTag("dusts/wolframite"), fluid, 90, HeatCondition.HEATED, 30);
        } else {
            meltingTag(metalName + "/dirty_dust", forgeItemTag("dirty_dusts/" + metalName), fluid, 90, HeatCondition.HEATED, 30);
            meltingTag(metalName + "/dust", forgeItemTag("dusts/" + metalName), fluid, 90, HeatCondition.HEATED, 20);
        }

        meltingTag(metalName + "/rod", forgeItemTag("rods/" + metalName), fluid, 45, HeatCondition.HEATED, 20);
        meltingTag(metalName + "/gear", forgeItemTag("gears/" + metalName), fluid, 360, HeatCondition.HEATED, 160);
        meltingTag(metalName + "/coin", forgeItemTag("coins/" + metalName), fluid, 10, HeatCondition.HEATED, 4);
        meltingTag(metalName + "/wire", forgeItemTag("wires/" + metalName), fluid, 45, HeatCondition.HEATED, 20);
        return null;
    }

    protected GeneratedRecipe moddedMetals() {
        for (CMCompatMetals metal : CMCompatMetals.values()) {
            String metalName = metal.getName();
            //Items
            meltingTag(metalName + "/ingot", forgeItemTag("ingots/" + metalName), metal.getFluid(), 90, HeatCondition.HEATED, 40);
            meltingTag(metalName + "/dirty_dust", forgeItemTag("dirty_dusts/" + metalName), metal.getFluid(), 90, HeatCondition.HEATED, 35);
            meltingTag(metalName + "/dust", forgeItemTag("dusts/" + metalName), metal.getFluid(), 90, HeatCondition.HEATED, 20);
            meltingTag(metalName + "/nugget", forgeItemTag("nuggets/" + metalName), metal.getFluid(), 10, HeatCondition.HEATED, 4);
            meltingTag(metalName + "/plate", forgeItemTag("plates/" + metalName), metal.getFluid(), 90, HeatCondition.HEATED, 40);
            meltingTag(metalName + "/rod", forgeItemTag("rods/" + metalName), metal.getFluid(), 45, HeatCondition.HEATED, 20);
            meltingTag(metalName + "/gear", forgeItemTag("gears/" + metalName), metal.getFluid(), 360, HeatCondition.HEATED, 160);
            meltingTag(metalName + "/coin", forgeItemTag("coins/" + metalName), metal.getFluid(), 10, HeatCondition.HEATED, 4);
            meltingTag(metalName + "/wire", forgeItemTag("wires/" + metalName), metal.getFluid(), 45, HeatCondition.HEATED, 20);
        }
        return null;
    }

    /**
     * Recipes with output Tags :
     *
     * @param recipeId Recipe name / folders
     * @param inputTag Input from tag
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingTag(String recipeId, TagKey<Item> inputTag, FluidEntry<ForgeFlowingFluid.Flowing> result, int amount, HeatCondition heatCondition, int duration) {
        return create(recipeId, b -> b.duration(duration)
                .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                .require(inputTag)
                .requiresHeat(heatCondition)
                .output(result.get(), amount));
    }


    /**
     * Recipes with output Items :
     *
     * @param input    Input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingItem(Supplier<ItemLike> input, FluidEntry<ForgeFlowingFluid.Flowing> result, int amount, HeatCondition heatCondition, int duration) {
        return create(input, b -> b.duration(duration)
                .requiresHeat(heatCondition)
                .output(result.get(), amount));
    }

    //

    public MeltingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.MELTING;
    }
}
