package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.Locale;

public class LANG {

    public static LangBuilder builder() {
        return new LangBuilder(CreateMetallurgy.MOD_ID);
    }

    public static MutableComponent translateDirect(String key, Object... args) {
        return Components.translatable(CreateMetallurgy.MOD_ID + "." + key, resolveBuilders(args));
    }

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getDisplayName()
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

    //

    public static Object[] resolveBuilders(Object[] args) {
        for (int i = 0; i < args.length; i++)
            if (args[i]instanceof LangBuilder cb)
                args[i] = cb.component();
        return args;
    }
}
