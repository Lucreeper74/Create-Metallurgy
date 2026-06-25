package fr.lucreeper74.createmetallurgy.config;

import net.createmod.catnip.config.ConfigBase;

public class CMClientConfig extends ConfigBase {

    // JEI
    public final ConfigBool showEntities = b(true, "showEntitiesInJEI",
            CMConfig.Comments.needReloadWorld,
            CMClientConfig.Comments.showEntities);

    // ---

    @Override
    public String getName() {
        return "client";
    }

    private static class Comments {
        static String showEntities = "Disable the display of entity ingredients in JEI/EMI.";
    }
}
