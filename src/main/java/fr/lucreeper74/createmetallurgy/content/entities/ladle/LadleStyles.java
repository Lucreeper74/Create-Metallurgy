package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LadleStyles {

    public static ResourceLocation getItemId(PackageStyle style) {
        String size = "_" + style.width() + "x" + style.height();
        String id = style.type() + (style.rare() ? "" : size);
        return CreateMetallurgy.genRL(id);
    }

    public static ResourceLocation getHandleModel(PackageStyle style) {
        String size = style.width() + "x" + style.height();
        return CreateMetallurgy.genRL("item/ladle/handle_" + size);
    }

    public static final List<PackageStyle> STYLES = ImmutableList.of(
            new PackageStyle("ladle", 12, 12, 23f, false)
    );

    public static final List<LadleItem> ALL_LADLES = new ArrayList<>();

    private static final Random STYLE_PICKER = new Random();

    public static ItemStack getRandomBox() {
        return new ItemStack(ALL_LADLES.get(STYLE_PICKER.nextInt(ALL_LADLES.size())));
    }

    public static ItemStack getDefault() {
        return new ItemStack(ALL_LADLES.get(0));
    }


}
