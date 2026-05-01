package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

@SuppressWarnings("unused")
public class CMSpriteShifts {
    public static final SpriteShiftEntry SAND_PAPER_BELT =
            get("block/grinder_belt/sand_paper", "block/grinder_belt/sand_paper_scroll"),
            RED_SAND_PAPER_BELT = get("block/grinder_belt/red_sand_paper", "block/grinder_belt/red_sand_paper_scroll");

    public static final CTSpriteShiftEntry INDUSTRIAL_LADLE = rectangleType("industrial_crucible/crucible"),
            INDUSTRIAL_LADLE_WINDOW = omniType("industrial_crucible/crucible_window"),
            INDUSTRIAL_LADLE_TOP = rectangleType("industrial_crucible/crucible_top"),
            INDUSTRIAL_LADLE_INNER = rectangleType("industrial_crucible/crucible_inner");

    //

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(CreateMetallurgy.asResource(originalLocation), CreateMetallurgy.asResource(targetLocation));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String name) {
        return CTSpriteShifter.getCT(type, CreateMetallurgy.asResource("block/" + name),
                CreateMetallurgy.asResource("block/" + name + "_connected"));
    }

    private static CTSpriteShiftEntry rectangleType(String name) {
        return getCT(AllCTTypes.RECTANGLE, name);
    }

    private static CTSpriteShiftEntry omniType(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    public static void init() {
        // init static fields
    }
}
