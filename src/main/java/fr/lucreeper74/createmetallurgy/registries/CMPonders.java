package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.ponders.CastingScenes;
import fr.lucreeper74.createmetallurgy.ponders.FoundryScenes;

public class CMPonders {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateMetallurgy.MOD_ID);

    public static void register() {
        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui

        HELPER.forComponents(CMBlocks.FOUNDRY_BASIN_BLOCK).addStoryBoard("foundry_basin", FoundryScenes::foundryBasin)
                .addStoryBoard("foundry_mixer", FoundryScenes::alloying);

        HELPER.forComponents(CMBlocks.FOUNDRY_MIXER_BLOCK).addStoryBoard("foundry_mixer", FoundryScenes::alloying);


        HELPER.forComponents(CMBlocks.CASTING_BASIN_BLOCK, CMBlocks.CASTING_TABLE_BLOCK).addStoryBoard("casting_blocks", CastingScenes::castingBlocks);
    }
}
