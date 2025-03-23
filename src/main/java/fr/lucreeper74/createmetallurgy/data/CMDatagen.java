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
import net.minecraftforge.data.event.GatherDataEvent;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();


        if (event.includeServer()) {

            gen.addProvider(true, new CMStandardRecipeGen(gen));
            gen.addProvider(true, new CastingRecipeGen(gen));
            gen.addProvider(true, new FoundryRecipeGen(gen));

            CMProcessingRecipesGen.registerAll(gen);

            CMPonders.register(); // Register before lang to insure loaded ponders during Datagen
            CMPonders.registerLang();
            AllPonderTags.register();

            REGISTRATE.addDataGenerator(ProviderType.LANG, CMLangGen::generate);
        }
    }
}