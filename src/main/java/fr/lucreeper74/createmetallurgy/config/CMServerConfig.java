package fr.lucreeper74.createmetallurgy.config;

import net.createmod.catnip.config.ConfigBase;

public class CMServerConfig extends ConfigBase {

    public final ConfigGroup ladle = group(0, "ladle", Comments.ladle);
    public final ConfigInt ladleCapacity = i(4000, 1, "ladleCapacity", Comments.mB, Comments.ladleCapacity);
    public final ConfigInt ladleMaxAddr = i(10, 0, "ladleMaxAddress", Comments.ladleMaxAddr);

    public final ConfigGroup crucible = group(0, "crucible", Comments.crucible);
    public final ConfigInt crucibleMaxHeight = i(4, 1, "crucibleMaxHeight", Comments.block, Comments.crucibleMaxHeight);
    public final ConfigInt crucibleMaxWidth = i(5, 1, "crucibleMaxWidth",  Comments.block, Comments.crucibleMaxWidth);
    public final ConfigInt crucibleCapacity = i(1, 1, "crucibleCapacity",  Comments.bucket, Comments.crucibleCapacity);

    // ---

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String mB = "[in milliBuckets (mB)]";
        static String bucket = "[in Buckets]";
        static String block = "[in Blocks]";

        static String ladle = "Ladle related configuration";
        static String ladleCapacity = "The amount of liquid a Ladle can hold.";
        static String ladleMaxAddr = "The number of addresses that can be written on a Ladle.";

        static String crucible = "Crucible related configuration";
        static String crucibleMaxHeight = "The maximum height a crucible can reach.";
        static String crucibleMaxWidth = "The maximum width a crucible can reach.";
        static String crucibleCapacity = "The amount of liquid a crucible can hold per block.";
    }
}
