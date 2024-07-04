package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import fr.lucreeper74.createmetallurgy.content.light_bulb.LightBulbBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.phys.Vec3;

public class LightBulbScenes {
    public static void lightBulbScenes(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("light_bulbs", "Using Light Bulbs");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos whiteBulbPos = util.grid.at(1, 1, 1);
        BlockPos redBulbPos = util.grid.at(4, 2, 3);
        BlockPos greenBulbPos = util.grid.at(2, 2, 3);
        BlockPos brownBulbPos = util.grid.at(0, 2, 3);
        BlockPos leverPos = util.grid.at(3, 1, 1);
        BlockPos redstonePos = leverPos.west();

        Selection whiteBulb = util.select.position(whiteBulbPos);
        Selection redBulb = util.select.position(redBulbPos);
        Selection greenBulb = util.select.position(greenBulbPos);
        Selection brownBulb = util.select.position(brownBulbPos);

        Vec3 whiteBulbVec = util.vector.blockSurface(whiteBulbPos, Direction.DOWN)
                .add(0, 12 / 16f, 0);
        Vec3 greenBulbVec = util.vector.blockSurface(greenBulbPos, Direction.DOWN)
                .add(0, 12 / 16f, 0);
        Vec3 leverVec = util.vector.centerOf(leverPos)
                .add(0, -.25, 0);

        //Intro
        scene.world.showSection(util.select.position(leverPos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(redstonePos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(whiteBulb, Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Light Bulbs are an alternative light source to the Redstone Lamp")
                .pointAt(whiteBulbVec)
                .placeNearTarget();
        scene.idle(90);

        //Turn on first bulb
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("They emit light proportionally to the strength of the redstone signal received")
                .pointAt(whiteBulbVec)
                .placeNearTarget();
        scene.overlay.showControls(new InputWindowElement(leverVec, Pointing.DOWN).rightClick(), 40);
        scene.idle(10);
        for (int i = 0; i < 8; i++) {
            scene.idle(5);
            final int state = i + 1;
            scene.world.modifyBlockEntityNBT(util.select.position(leverPos), AnalogLeverBlockEntity.class,
                    nbt -> nbt.putInt("State", state));
            scene.world.modifyBlock(redstonePos, s -> s.setValue(RedStoneWireBlock.POWER, state), false);
            scene.effects.indicateRedstone(redstonePos);
            scene.world.modifyBlock(whiteBulbPos, s -> s.setValue(LightBulbBlock.LEVEL, state), false);
        }
        scene.idle(40);

        //Add others bulbs
        scene.world.modifyBlockEntityNBT(util.select.position(leverPos), AnalogLeverBlockEntity.class,
                nbt -> nbt.putInt("State", 0));
        scene.world.modifyBlock(redstonePos, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);
        scene.effects.indicateRedstone(redstonePos);
        scene.world.modifyBlock(whiteBulbPos, s -> s.setValue(LightBulbBlock.LEVEL, 0), false);
        scene.idle(20);
        scene.world.showSection(util.select.fromTo(4, 1, 3, 0, 1, 3), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(redBulb, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(greenBulb, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(brownBulb, Direction.DOWN);
        scene.idle(10);

        scene.overlay.showOutline(PonderPalette.WHITE, new Object(), redBulb, 80);
        scene.overlay.showOutline(PonderPalette.WHITE, new Object(), greenBulb, 80);
        scene.overlay.showOutline(PonderPalette.WHITE, new Object(), brownBulb, 80);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Light Bulbs are available in all colors")
                .pointAt(greenBulbVec)
                .placeNearTarget();
        scene.idle(90);

        scene.overlay.showFilterSlotInput(whiteBulbVec, Direction.UP, 100);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Placing items in Address slot can connect others light bulbs in a Network")
                .pointAt(whiteBulbVec)
                .placeNearTarget();
        scene.idle(90);
    }
}