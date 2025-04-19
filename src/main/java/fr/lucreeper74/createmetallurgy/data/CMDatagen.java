package fr.lucreeper74.createmetallurgy.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipes.vanilla.CMStandardRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.CastingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.FoundryRecipeGen;
import fr.lucreeper74.createmetallurgy.ponders.CMPonders;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public class CMDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        addExtraRegistrateData();

        if (event.includeServer()) {
            gen.addProvider(true, new CMStandardRecipeGen(output));
            gen.addProvider(true, new CastingRecipeGen(output));
            gen.addProvider(true, new FoundryRecipeGen(output));

            CMProcessingRecipesGen.registerAll(gen, output);
            gen.addProvider(event.includeServer(), new CMGenEntriesProvider(output, event.getLookupProvider()));
        }
    }

    private static void addExtraRegistrateData() {
        CreateMetallurgy.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            provideDefaultLang("interface", langConsumer);
            provideDefaultLang("tooltips", langConsumer);
            providePonderLang(langConsumer);
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/createmetallurgy/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new CMPonders());
        PonderIndex.getLangAccess().provideLang(CreateMetallurgy.MOD_ID, consumer);
    }
}