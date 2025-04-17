package fr.lucreeper74.createmetallurgy.data;

import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.tterrag.registrate.providers.ProviderType;
import fr.lucreeper74.createmetallurgy.data.lang.CMLangGen;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipes.vanilla.CMStandardRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.CastingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.FoundryRecipeGen;
import fr.lucreeper74.createmetallurgy.registries.CMPonders;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();


        if (event.includeServer()) {
            CMPonders.register(); // Register before lang cause not run during datagen

            gen.addProvider(true, new CMStandardRecipeGen(output));
            gen.addProvider(true, new CastingRecipeGen(output));
            gen.addProvider(true, new FoundryRecipeGen(output));

            CMProcessingRecipesGen.registerAll(gen, output);

            CMPonders.registerLang();
            AllPonderTags.register();

            REGISTRATE.addDataGenerator(ProviderType.LANG, CMLangGen::generate);
            gen.addProvider(event.includeServer(), new CMGenEntriesProvider(output, event.getLookupProvider()));
        }
    }
}