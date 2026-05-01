package fr.lucreeper74.createmetallurgy.utils;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Locale;

public class CMLang extends Lang {

    public static LangBuilder builder() {
        return new LangBuilder(CreateMetallurgy.MOD_ID);
    }

    public static MutableComponent translateDirect(String key, Object... args) {
        return Component.translatable(CreateMetallurgy.MOD_ID + "." + key, LangBuilder.resolveBuilders(args));
    }

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getHoverName()
                .copy());
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }
}
