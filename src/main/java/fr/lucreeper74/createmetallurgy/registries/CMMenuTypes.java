package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterMenu;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CMMenuTypes {
    public static final MenuEntry<LadleFilterMenu> LADLE_FILTER =
            register("ladle_filter", LadleFilterMenu::new, () -> LadleFilterScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return CreateMetallurgy.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}
}