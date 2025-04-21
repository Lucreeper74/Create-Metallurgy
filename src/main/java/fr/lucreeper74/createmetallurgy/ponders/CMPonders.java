package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import static fr.lucreeper74.createmetallurgy.ponders.CMPonderTags.METALWORK;

public class CMPonders implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateMetallurgy.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerScenes(helper);
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui

        HELPER.forComponents(CMBlocks.FOUNDRY_BASIN_BLOCK)
                .addStoryBoard("foundry_basin", FoundryScenes::foundryBasin, CMPonderTags.METALWORK)
                .addStoryBoard("foundry_mixer", FoundryScenes::alloying, CMPonderTags.METALWORK);

        HELPER.forComponents(CMBlocks.FOUNDRY_MIXER_BLOCK)
                .addStoryBoard("foundry_mixer", FoundryScenes::alloying, CMPonderTags.METALWORK);


        HELPER.forComponents(CMBlocks.CASTING_BASIN_BLOCK, CMBlocks.CASTING_TABLE_BLOCK)
                .addStoryBoard("casting_blocks", CastingScenes::castingBlocks, CMPonderTags.METALWORK);

        HELPER.forComponents(CMBlocks.INDUSTRIAL_CRUCIBLE)
                .addStoryBoard("industrial_crucible", CrucibleScenes::crucible, CMPonderTags.METALWORK)
                .addStoryBoard("foundry", CrucibleScenes::foundry, CMPonderTags.METALWORK);

        HELPER.forComponents(CMBlocks.LIGHT_BULBS.toArray())
                .addStoryBoard("light_bulbs", LightBulbScenes::lightBulbScenes, CMPonderTags.METALWORK);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerTags(helper);
        CMPonderTags.register(helper);
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addToTag(METALWORK)
                .add(CMBlocks.FOUNDRY_BASIN_BLOCK)
                .add(CMBlocks.FOUNDRY_LID_BLOCK)
                .add(CMBlocks.FOUNDRY_MIXER_BLOCK)
                .add(CMBlocks.CASTING_TABLE_BLOCK)
                .add(CMBlocks.CASTING_BASIN_BLOCK)
                .add(CMBlocks.BELT_GRINDER_BLOCK)
                .add(CMBlocks.INDUSTRIAL_CRUCIBLE);

        HELPER.addToTag(AllCreatePonderTags.REDSTONE)
                .add(CMBlocks.LIGHT_BULBS.get(DyeColor.WHITE));
    }
}
