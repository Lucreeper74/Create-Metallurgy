package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.world.item.DyeColor;

public class CMPonderTags {

        public static final PonderTag METALWORK = create("metalwork").item(CMBlocks.FOUNDRY_MIXER_BLOCK.get(), true, false)
                .defaultLang("Metalwork", "Components which used to work metals")
                .addToIndex();

        private static PonderTag create(String id) {
            return new PonderTag(CreateMetallurgy.genRL(id));
        }

    public static void register() {
        PonderRegistry.TAGS.forTag(METALWORK)
                .add(CMBlocks.FOUNDRY_BASIN_BLOCK)
                .add(CMBlocks.FOUNDRY_LID_BLOCK)
                .add(CMBlocks.FOUNDRY_MIXER_BLOCK)
                .add(CMBlocks.GLASSED_FOUNDRY_LID_BLOCK)
                .add(CMBlocks.CASTING_TABLE_BLOCK)
                .add(CMBlocks.CASTING_BASIN_BLOCK)
                .add(CMBlocks.BELT_GRINDER_BLOCK);

        PonderRegistry.TAGS.forTag(AllPonderTags.REDSTONE)
                .add(CMBlocks.LIGHT_BULBS.get(DyeColor.WHITE));
    }
}
