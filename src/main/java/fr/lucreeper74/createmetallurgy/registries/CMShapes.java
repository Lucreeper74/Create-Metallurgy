package fr.lucreeper74.createmetallurgy.registries;

import static net.minecraft.core.Direction.NORTH;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CMShapes {

    // Variable Block Shapes
    public static final VoxelShaper

            LIGHT_BULB = shape(5, 0, 5, 11, 12, 11).forDirectional(),
            FAUCET = shape(4, 6, 8, 12, 11, 15).forDirectional(NORTH),

    CRUCIBLE_CORNER = shape(0, 0, 0, 16, 16, 16).erase(0, 0, 3, 13, 16, 16).forHorizontal(NORTH),
            CRUCIBLE_WALL = shape(0, 0, 0, 16, 16, 3).forHorizontal(NORTH),
            CRUCIBLE_CORNER_BOTTOM = shape(0, 0, 0, 16, 16, 16).erase(0, 0, 3, 13, 16, 16).add(0, 0, 0, 16, 4, 16).forHorizontal(NORTH),
            CRUCIBLE_WALL_BOTTOM = shape(0, 0, 0, 16, 16, 3).add(0, 0, 0, 16, 4, 16).forHorizontal(NORTH);

    // Static Block Shapes
    public static final VoxelShape
            CRUCIBLE_SINGLE = shape(0, 0, 0, 16, 16, 16).erase(3, 0, 3, 13, 16, 13).build(),
            CRUCIBLE_SINGLE_BOTTOM = shape(0, 0, 0, 16, 16, 16).erase(3, 0, 3, 13, 16, 13).add(0, 0, 0, 16, 4, 16).build(),
            CRUCIBLE_BOTTOM = shape(0, 0, 0, 16, 4, 16).build(),

            FOUNDRY_LID = shape(1, 0, 1, 15, 14, 15).add(3, 13, 3, 13, 15, 13).build(),
            FAUCET_DOWN = shape(4, 8, 4, 12, 16, 12).build();

    //

    public static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    public static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}