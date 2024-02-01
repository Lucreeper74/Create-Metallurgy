package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class CastingScenes {
    public static void castingBlocks(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("casting_blocks", "Casting Molten Metals");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos basinPos = util.grid.at(3, 1, 2);
        BlockPos tablePos = util.grid.at(1, 1, 2);

        //Intro
        scene.world.showSection(util.select.position(basinPos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(basinPos.north()), Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(tablePos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(tablePos.north()), Direction.SOUTH);
        scene.idle(5);
    }
}