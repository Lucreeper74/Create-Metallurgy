package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.core.BlockPos;

public class CrucibleScenes {

    public static void crucible(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("industrial_crucible", "Build up Industrial Crucible");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos depotPos = util.grid.at(2, 1, 1);
        BlockPos tablePos = depotPos.east();
    }
}
