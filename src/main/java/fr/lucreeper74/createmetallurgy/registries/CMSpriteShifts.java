package fr.lucreeper74.createmetallurgy.registries;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

@SuppressWarnings("unused")
public class CMSpriteShifts {
    public static final SpriteShiftEntry SAND_PAPER_BELT =
            get("block/grinder_belt/sand_paper", "block/grinder_belt/sand_paper_scroll"),
            RED_SAND_PAPER_BELT = get("block/grinder_belt/red_sand_paper", "block/grinder_belt/red_sand_paper_scroll");

    //

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(CreateMetallurgy.genRL(originalLocation), CreateMetallurgy.genRL(targetLocation));
    }

    public static void init() {
        // init static fields
    }
}
