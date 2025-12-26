package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Random;

public class LadleStyles {

    public static ResourceLocation getStyleId(PackageStyle style) {
        return CreateMetallurgy.genRL(style.type());
    }

    public static ResourceLocation getHandleModel() {
        return CreateMetallurgy.genRL("item/ladle/handle");
    }

    public static final List<PackageStyle> STANDARD_LADLES_STYLES =  ImmutableList.of(
            new PackageStyle("ghast", 12, 12, 23f, false),  // Credit: Aaby
            new PackageStyle("cute", 12, 12, 23f, false),   // Credit: RestingPhantom
            new PackageStyle("old", 12, 12, 23f, false),    // Credit: Aaby
            new PackageStyle("strider", 12, 12, 23f, false)
    );

    public static final List<PackageStyle> RARE_LADLES_STYLES = ImmutableList.of(
            community("restingphantom"),
            community("the_cooler")   // Credit: RestingPhantom
    );

    public static final List<PackageStyle> ALL_STYLES = ImmutableList.<PackageStyle> builder()
                    .addAll(STANDARD_LADLES_STYLES)
                    .addAll(RARE_LADLES_STYLES)
                    .build();

    private static final Random STYLE_PICKER = new Random();
    private static final int RARE_CHANCE = 1000;

    public static PackageStyle getRandomStyle() {
        List<PackageStyle> pool = STYLE_PICKER.nextInt(RARE_CHANCE) == 0 ? RARE_LADLES_STYLES : STANDARD_LADLES_STYLES;
        return pool.get(STYLE_PICKER.nextInt(pool.size()));
    }

    public static PackageStyle getDefaultStyle() {
        return ALL_STYLES.get(0);
    }

    private static PackageStyle community(String name) {
        return new PackageStyle(name, 12, 12, 23f, true);
    }
}
