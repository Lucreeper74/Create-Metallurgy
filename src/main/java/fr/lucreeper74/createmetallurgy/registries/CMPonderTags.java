package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CMPonderTags {
    public static final ResourceLocation METALWORK = new ResourceLocation(CreateMetallurgy.MOD_ID, "metalwork");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        helper.registerTag(METALWORK).addToIndex().item(CMBlocks.FOUNDRY_MIXER_BLOCK.get().asItem(), true, false)
                .title("Metalwork").description("Components which used to work metals").register();
        HELPER.addToTag(METALWORK)
                .add(CMBlocks.FOUNDRY_BASIN_BLOCK)
                .add(CMBlocks.FOUNDRY_LID_BLOCK)
                .add(CMBlocks.FOUNDRY_MIXER_BLOCK)
                .add(CMBlocks.GLASSED_FOUNDRY_LID_BLOCK)
                .add(CMBlocks.CASTING_TABLE_BLOCK)
                .add(CMBlocks.CASTING_BASIN_BLOCK)
                .add(CMBlocks.BELT_GRINDER_BLOCK);

        HELPER.addToTag(AllCreatePonderTags.REDSTONE)
                .add(CMBlocks.LIGHT_BULBS.get(DyeColor.WHITE));
    }
}
