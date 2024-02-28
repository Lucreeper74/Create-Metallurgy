package fr.lucreeper74.createmetallurgy.registries;

import com.jozufozu.flywheel.core.PartialModel;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;

public class AllPModels {

    public static final PartialModel

            FOUNDRY_MIXER_POLE = block("foundry_mixer/pole"),
            FOUNDRY_MIXER_HEAD = block("foundry_mixer/head"),
            SHAFTLESS_STONE_COGWHEEL = block("foundry_mixer/cog"),

            THERMOMETER_GAUGE = block("gauges/thermometer");

    private static PartialModel block(String path) {
        return new PartialModel(CreateMetallurgy.genRL("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
