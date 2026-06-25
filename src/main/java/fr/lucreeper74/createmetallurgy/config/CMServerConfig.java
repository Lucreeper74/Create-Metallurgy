package fr.lucreeper74.createmetallurgy.config;

import net.createmod.catnip.config.ConfigBase;

public class CMServerConfig extends ConfigBase {

    public final ConfigGroup ladle = group(0, "ladle", Comments.ladle);
    public final ConfigInt ladleCapacity = i(4000, 1, "ladleCapacity", Comments.mB, Comments.ladleCapacity);
    public final ConfigInt ladleMaxAddr = i(10, 0, "ladleMaxAddress", Comments.ladleMaxAddr);

    // ---

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String mB = "[in milliBuckets (mB)]";

        static String ladle = "Ladle related configuration";
        static String ladleCapacity = "The amount of liquid a Ladle can hold.";
        static String ladleMaxAddr = "The number of addresses that can be written on a Ladle.";
    }
}
