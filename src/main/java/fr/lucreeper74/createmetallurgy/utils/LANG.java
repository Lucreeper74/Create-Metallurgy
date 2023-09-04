package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraftforge.fluids.FluidStack;

public class LANG {

    public static LangBuilder builder() {
        return new LangBuilder(CreateMetallurgy.MOD_ID);
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
}
