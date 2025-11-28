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
        return CreateMetallurgy.genRL(style.type() + "_ladle");
    }

    public static ResourceLocation getHandleModel() {
        return CreateMetallurgy.genRL("item/ladle/handle");
    }

    public static final List<PackageStyle> STYLES = ImmutableList.of(
            new PackageStyle("ghast", 12, 12, 23f, false),  // Credit: Aaby
            new PackageStyle("cute", 12, 12, 23f, false),   // Credit: RestingPhantom
            new PackageStyle("old", 12, 12, 23f, false),    // Credit: Aaby
            new PackageStyle("strider", 12, 12, 23f, false),

            community("restingphantom"),
            community("the_cooler")   // Credit: RestingPhantom
    );

    public static final List<LadleItem> ALL_LADLES = new ArrayList<>();

    private static final Random STYLE_PICKER = new Random();

    public static ItemStack getRandomBox() {
        return new ItemStack(ALL_LADLES.get(STYLE_PICKER.nextInt(ALL_LADLES.size())));
    }

    public static ItemStack getDefault() {
        return new ItemStack(ALL_LADLES.get(0));
    }

    private static PackageStyle community(String name) {
        return new PackageStyle(name, 12, 12, 23f, true);
    }
}
