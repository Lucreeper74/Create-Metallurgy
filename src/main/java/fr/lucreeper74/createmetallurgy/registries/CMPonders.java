package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.ponders.CastingScenes;
import fr.lucreeper74.createmetallurgy.ponders.FoundryScenes;
import fr.lucreeper74.createmetallurgy.ponders.LightBulbScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.minecraft.resources.ResourceLocation;

public class CMPonders {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CMBlocks.FOUNDRY_BASIN_BLOCK)
                .addStoryBoard("foundry_basin", FoundryScenes::foundryBasin, CMPonderTags.METALWORK)
                .addStoryBoard("foundry_mixer", FoundryScenes::alloying, CMPonderTags.METALWORK);

        HELPER.forComponents(CMBlocks.FOUNDRY_MIXER_BLOCK)
                .addStoryBoard("foundry_mixer", FoundryScenes::alloying, CMPonderTags.METALWORK);


        HELPER.forComponents(CMBlocks.CASTING_BASIN_BLOCK, CMBlocks.CASTING_TABLE_BLOCK)
                .addStoryBoard("casting_blocks", CastingScenes::castingBlocks, CMPonderTags.METALWORK);

        HELPER.forComponents(CMBlocks.LIGHT_BULBS.toArray())
                .addStoryBoard("light_bulbs", LightBulbScenes::lightBulbScenes, AllCreatePonderTags.REDSTONE);
    }

    public static void registerLang() {
    }
}
