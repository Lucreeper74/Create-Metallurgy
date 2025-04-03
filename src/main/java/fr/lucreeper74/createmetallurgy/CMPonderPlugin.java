package fr.lucreeper74.createmetallurgy;

import fr.lucreeper74.createmetallurgy.registries.CMPonderTags;
import fr.lucreeper74.createmetallurgy.registries.CMPonders;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CMPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreateMetallurgy.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CMPonders.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CMPonderTags.register(helper);
    }

}
