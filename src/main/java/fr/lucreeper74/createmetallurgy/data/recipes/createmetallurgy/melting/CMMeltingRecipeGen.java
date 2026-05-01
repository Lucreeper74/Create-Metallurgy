package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.melting;

import com.simibubi.create.AllItems;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMMeltingRecipeGen extends MeltingRecipeGen {

    public CMMeltingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateMetallurgy.MOD_ID);
    }

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
}
