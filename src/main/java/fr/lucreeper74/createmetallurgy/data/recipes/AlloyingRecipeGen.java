package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.ForgeFlowingFluid;

@SuppressWarnings("unused")
public class AlloyingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            STEEL = basicAlloy("steel", CMFluids.MOLTEN_IRON, 270, T.coke(), CMFluids.MOLTEN_STEEL, 270, HeatCondition.HEATED, 40),
            BRASS = basicAlloy("brass", CMFluids.MOLTEN_COPPER, 10, CMFluids.MOLTEN_ZINC, 10, CMFluids.MOLTEN_BRASS, 20, HeatCondition.HEATED, 40),
            INVAR = basicAlloy("invar", CMFluids.MOLTEN_IRON, 20, CMFluids.MOLTEN_NICKEL, 10, CMFluids.MOLTEN_INVAR, 30, HeatCondition.HEATED, 40),
            ELECTRUM = basicAlloy("electrum", CMFluids.MOLTEN_GOLD, 10, CMFluids.MOLTEN_SILVER, 10, CMFluids.MOLTEN_ELECTRUM, 30, HeatCondition.HEATED, 40),
            BRONZE = basicAlloy("bronze", CMFluids.MOLTEN_COPPER, 30, CMFluids.MOLTEN_TIN, 10, CMFluids.MOLTEN_BRONZE, 40, HeatCondition.HEATED, 40),
            CONSTANTAN = basicAlloy("constantan", CMFluids.MOLTEN_COPPER, 10, CMFluids.MOLTEN_NICKEL, 10, CMFluids.MOLTEN_CONSTANTAN, 20, HeatCondition.HEATED, 40),

            NETHERITE = create("netherite", b -> b.require(Items.NETHERITE_SCRAP)
                .require(Items.NETHERITE_SCRAP)
                .require(CMFluids.MOLTEN_GOLD.get(), 120)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(CMFluids.MOLTEN_NETHERITE.get(), 45)),

            VOID_STEEL = create("void_steel", b -> b.require(Items.ENDER_PEARL)
                .require(CMFluids.MOLTEN_NETHERITE.get(), 90)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(CMFluids.MOLTEN_VOID_STEEL.get(), 90));

    //

    /**
     * Recipe for basic Alloys from 2 Fluids :
     *
     * @param recipeId     Recipe name / folders
     * @param fluid1       Input first
     * @param amount1      First amount
     * @param fluid2       Input second
     * @param amount2      Second amount
     * @param result       Result fluid
     * @param amountResult Result amount
     * @param duration     Processing time
     */
    protected GeneratedRecipe basicAlloy(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid1, int amount1, FluidEntry<ForgeFlowingFluid.Flowing> fluid2, int amount2, FluidEntry<ForgeFlowingFluid.Flowing> result, int amountResult, HeatCondition heatCondition, int duration) {
        return create(recipeId, b -> b.duration(duration)
                .require(fluid1.get(), amount1)
                .require(fluid2.get(), amount2)
                .requiresHeat(heatCondition)
                .output(result.get(), amountResult));
    }

    /**
     * Recipe for basic Alloys from Fluid + Item (Tag) :
     *
     * @param recipeId     Recipe name / folders
     * @param fluid        Input first
     * @param amount       First amount
     * @param itemTag      Input tag
     * @param result       Result fluid
     * @param amountResult Result amount
     * @param duration     Processing time
     */
    protected GeneratedRecipe basicAlloy(String recipeId, FluidEntry<ForgeFlowingFluid.Flowing> fluid, int amount, TagKey<Item> itemTag, FluidEntry<ForgeFlowingFluid.Flowing> result, int amountResult, HeatCondition heatCondition, int duration) {
        return create(recipeId, b -> b.duration(duration)
                .require(fluid.get(), amount)
                .require(itemTag)
                .requiresHeat(heatCondition)
                .output(result.get(), amountResult));
    }

    //

    public AlloyingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.ALLOYING;
    }
}
