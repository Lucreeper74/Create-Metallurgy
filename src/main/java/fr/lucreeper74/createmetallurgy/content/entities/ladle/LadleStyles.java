package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LadleStyles {

    public static ResourceLocation getItemId(PackageStyle style) {
        return CreateMetallurgy.asResource(style.type() + "_transfer_ladle");
    }

    public static ResourceLocation getHandleModel() {
        return CreateMetallurgy.asResource("item/ladle/handle");
    }

    public static final List<PackageStyle> LADLES_STYLES =  ImmutableList.of(
            new PackageStyle("ghast", 12, 12, 23f, false),  // Credit: Aaby
            new PackageStyle("cute", 12, 12, 23f, false),   // Credit: RestingPhantom
            new PackageStyle("old", 12, 12, 23f, false),    // Credit: Aaby
            new PackageStyle("strider", 12, 12, 23f, false),

            community("restingphantom"),
            community("the_cooler")   // Credit: RestingPhantom
    );
    public static final List<LadleItem> ALL_LADLES = new ArrayList<>();
    public static final List<LadleItem> STANDARD_LADLES = new ArrayList<>();
    public static final List<LadleItem> RARE_LADLES = new ArrayList<>();

    private static final Random STYLE_PICKER = new Random();
    public static final int RARE_CHANCE = 1000;

    public static ItemStack getRandomLadle() {
        List<LadleItem> pool = STYLE_PICKER.nextInt(RARE_CHANCE) == 0 ? RARE_LADLES : STANDARD_LADLES;
        return new ItemStack(pool.get(STYLE_PICKER.nextInt(pool.size())));
    }

    public static ItemStack getDefault() {
        return new ItemStack(ALL_LADLES.get(0));
    }

    private static PackageStyle community(String name) {
        return new PackageStyle(name, 12, 12, 23f, true);
    }
}
