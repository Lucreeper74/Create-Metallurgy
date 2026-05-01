package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.foundation.utility.DyeHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class CMDyeHelper extends DyeHelper {

    public static ItemLike getGlassOfDye(DyeColor color) {
        return switch (color) {
            case BLACK -> Blocks.BLACK_STAINED_GLASS;
            case BLUE -> Blocks.BLUE_STAINED_GLASS;
            case BROWN -> Blocks.BROWN_STAINED_GLASS;
            case CYAN -> Blocks.CYAN_STAINED_GLASS;
            case GRAY -> Blocks.GRAY_STAINED_GLASS;
            case GREEN -> Blocks.GREEN_STAINED_GLASS;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS;
            case LIME -> Blocks.LIME_STAINED_GLASS;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS;
            case PINK -> Blocks.PINK_STAINED_GLASS;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS;
            case RED -> Blocks.RED_STAINED_GLASS;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS;
            default -> Blocks.WHITE_STAINED_GLASS;
        };
    }
}
