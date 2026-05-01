package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CMPonderTags {

    public static final ResourceLocation

            METALWORK = resource("metalwork");

    private static ResourceLocation resource(String id) {
        return CreateMetallurgy.asResource(id);
    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        helper.registerTag(METALWORK)
                .addToIndex()
                .item(AllBlocks.COGWHEEL.get(), true, false)
                .title("Metalwork")
                .description("Components which used to work metals")
                .register();

        //

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
