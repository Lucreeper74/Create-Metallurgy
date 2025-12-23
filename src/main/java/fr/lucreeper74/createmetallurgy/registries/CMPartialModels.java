package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.*;

public class CMPartialModels {

    public static final PartialModel

            FOUNDRY_MIXER_POLE = block("foundry_mixer/pole"),
            FOUNDRY_MIXER_HEAD = block("foundry_mixer/head"),
            SHAFTLESS_STONE_COGWHEEL = block("foundry_mixer/cog"),

            THERMOMETER_GAUGE = block("gauges/thermometer"),
            THERMOMETER_DIAL = block("gauges/dial"),

            GRINDER_BELT = block("mechanical_belt_grinder/belt"),

            BULB_INNER_GLOW = block("light_bulb/inner_glow"),

            LABELLING_STATION_TRAY_REGULAR = block("labelling_station/tray"),
                LABELLING_STATION_TRAY_DEFRAG = block("labelling_station/tray"),
                LABELLING_STATION_HATCH_OPEN = block("labelling_station/hatch_open"),
                LABELLING_STATION_HATCH_CLOSED = block("labelling_station/hatch_closed"),


    // JEI Gui models

            JEI_CURCIBLE_2X2 = jei("assembled_crucible");

    public static final Map<DyeColor, PartialModel> BULB_TUBES = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, PartialModel> BULB_TUBES_GLOW = new EnumMap<>(DyeColor.class);
    static {
        for (DyeColor color : DyeColor.values()) {
            BULB_TUBES.put(color, block("light_bulb/tube/" + CMLang.asId(color.name())));
            BULB_TUBES_GLOW.put(color, block("light_bulb/tube_glow/" + CMLang.asId(color.name())));
        }
    }
//
//    public static final Map<ResourceLocation, PartialModel> LADLES = new HashMap<>();
//    public static final List<PartialModel> LADLES_TO_HIDE_AS = new ArrayList<>();
//    public static final Map<ResourceLocation, PartialModel> LADLE_HANDLE = new HashMap<>();

    static {
        for (PackageStyle style : LadleStyles.STYLES) {
            ResourceLocation key = LadleStyles.getItemId(style);
            PartialModel model = PartialModel.of(CreateMetallurgy.genRL("item/" + key.getPath()));
            AllPartialModels.PACKAGES.put(key, model);
            AllPartialModels.PACKAGE_RIGGING.put(key, PartialModel.of(LadleStyles.getHandleModel()));
        }
    }

    private static PartialModel block(String path) {
        return PartialModel.of(CreateMetallurgy.genRL("block/" + path));
    }
    private static PartialModel jei(String path) {
        return PartialModel.of(CreateMetallurgy.genRL("jei/" + path));
    }

    public static void init() {
        // init static fields
    }
}
