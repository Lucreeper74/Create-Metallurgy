package fr.lucreeper74.createmetallurgy.data;

import com.simibubi.create.AllTags;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.providers.ProviderType;
import fr.lucreeper74.createmetallurgy.CMPonderPlugin;
import fr.lucreeper74.createmetallurgy.data.lang.CMLangGen;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipes.CastingRecipeGen;
import fr.lucreeper74.createmetallurgy.registries.CMPonders;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        PonderIndex.addPlugin(new CMPonderPlugin());

        if (event.includeServer()) {
            gen.addProvider(true, new CastingRecipeGen(output));

            CMProcessingRecipesGen.registerAll(gen, output);

            CMPonders.registerLang();
            AllTags.init();

            REGISTRATE.addDataGenerator(ProviderType.LANG, CMLangGen::generate);
            gen.addProvider(event.includeServer(), new CMGenEntriesProvider(output, event.getLookupProvider()));
        }
    }
}