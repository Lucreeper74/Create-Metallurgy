package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.foundation.utility.DyeHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class CMDyeHelper extends DyeHelper {

    public static ItemLike getGlassOfDye(DyeColor color) {
        switch (color) {
            case BLACK:
                return Blocks.BLACK_STAINED_GLASS;
            case BLUE:
                return Blocks.BLUE_STAINED_GLASS;
            case BROWN:
                return Blocks.BROWN_STAINED_GLASS;
            case CYAN:
                return Blocks.CYAN_STAINED_GLASS;
            case GRAY:
                return Blocks.GRAY_STAINED_GLASS;
            case GREEN:
                return Blocks.GREEN_STAINED_GLASS;
            case LIGHT_BLUE:
                return Blocks.LIGHT_BLUE_STAINED_GLASS;
            case LIGHT_GRAY:
                return Blocks.LIGHT_GRAY_STAINED_GLASS;
            case LIME:
                return Blocks.LIME_STAINED_GLASS;
            case MAGENTA:
                return Blocks.MAGENTA_STAINED_GLASS;
            case ORANGE:
                return Blocks.ORANGE_STAINED_GLASS;
            case PINK:
                return Blocks.PINK_STAINED_GLASS;
            case PURPLE:
                return Blocks.PURPLE_STAINED_GLASS;
            case RED:
                return Blocks.RED_STAINED_GLASS;
            case YELLOW:
                return Blocks.YELLOW_STAINED_GLASS;
            case WHITE:
            default:
                return Blocks.WHITE_STAINED_GLASS;
        }
    }
}
