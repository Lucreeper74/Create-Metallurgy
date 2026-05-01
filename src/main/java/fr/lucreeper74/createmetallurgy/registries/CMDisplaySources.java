package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryDisplaySource;

import java.util.function.Supplier;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

public class CMDisplaySources {

    public static final RegistryEntry<DisplaySource, FoundryDisplaySource> FOUNDRY_STATUS = simple("foundry_status", FoundryDisplaySource::new);

    //

    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }

    public static void register() {}
}
