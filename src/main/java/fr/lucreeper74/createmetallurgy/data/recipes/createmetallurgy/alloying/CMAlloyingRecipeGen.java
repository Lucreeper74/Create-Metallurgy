package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.alloying;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider.T;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMAlloyingRecipeGen extends AlloyingRecipeGen {

    public CMAlloyingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            STEEL = basicAlloy("steel", CMFluids.MOLTEN_IRON, 270, T.coke(), CMFluids.MOLTEN_STEEL, 270, HeatCondition.HEATED, 40),
            BRASS = basicAlloy("brass", CMFluids.MOLTEN_COPPER, 10, CMFluids.MOLTEN_ZINC, 10, CMFluids.MOLTEN_BRASS, 20, HeatCondition.HEATED, 40),
            INVAR = basicAlloy("invar", CMFluids.MOLTEN_IRON, 20, CMFluids.MOLTEN_NICKEL, 10, CMFluids.MOLTEN_INVAR, 30, HeatCondition.HEATED, 40),
            ELECTRUM = basicAlloy("electrum", CMFluids.MOLTEN_GOLD, 10, CMFluids.MOLTEN_SILVER, 10, CMFluids.MOLTEN_ELECTRUM, 20, HeatCondition.HEATED, 40),
            BRONZE = basicAlloy("bronze", CMFluids.MOLTEN_COPPER, 30, CMFluids.MOLTEN_TIN, 10, CMFluids.MOLTEN_BRONZE, 40, HeatCondition.HEATED, 40),
            CONSTANTAN = basicAlloy("constantan", CMFluids.MOLTEN_COPPER, 10, CMFluids.MOLTEN_NICKEL, 10, CMFluids.MOLTEN_CONSTANTAN, 20, HeatCondition.HEATED, 40),


            OBDURIUM = create("obdurium", b -> b.require(AllItems.ANDESITE_ALLOY.get())
                .require(CMFluids.MOLTEN_TUNGSTEN.get(), 60)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(CMFluids.MOLTEN_OBDURIUM.get(), 150)),

            NETHERITE = create("netherite", b -> b.require(Items.NETHERITE_SCRAP)
                .require(Items.NETHERITE_SCRAP)
                .require(CMFluids.MOLTEN_GOLD.get(), 120)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(CMFluids.MOLTEN_NETHERITE.get(), 45)),

            VOID_STEEL = create("void_steel", b -> b.require(Items.ENDER_PEARL)
                .require(CMFluids.MOLTEN_NETHERITE.get(), 90)
                .requiresHeat(HeatCondition.SUPERHEATED)
                .output(CMFluids.MOLTEN_VOID_STEEL.get(), 90)),

            NECROMIUM = create("necromium", b -> b.require(Items.NETHERITE_SCRAP)
                    .require(Items.NETHERITE_SCRAP)
                    .require(CMFluids.MOLTEN_SILVER.get(), 120)
                    .requiresHeat(HeatCondition.SUPERHEATED)
                    .output(CMFluids.MOLTEN_NECROMIUM.get(), 45))

    ;
}
