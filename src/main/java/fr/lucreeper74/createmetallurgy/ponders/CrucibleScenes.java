package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.*;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlock;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class CrucibleScenes {

    public static void crucible(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("industrial_crucible", "Build up Industrial Crucible");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        Selection crucible = util.select().fromTo(2, 1, 2, 4, 3, 4);
        Selection largeCog1 = util.select().position(2, 0, 6);
        Selection shaft = util.select().fromTo(1, 1, 4, 1, 1, 6);
        Selection pipe = util.select().fromTo(0, 1, 3, 0, 1, 6)
                .add(util.select().position(1, 1, 3))
                .add(util.select().position(0, 0, 6));

        BlockPos crucibleController = util.grid().at(2, 2, 2);

        // Intro
        scene.world().showIndependentSection(crucible, Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Industrial Crucibles can be used to store molten metals")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 3, 2), Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("You can add windows with a Wrench!")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 3, 2), Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);


        // Show windows configuration on small crucible
        scene.overlay().showControls(util.vector().blockSurface(crucibleController, Direction.WEST), Pointing.LEFT, 15)
                .rightClick().withItem(AllItems.WRENCH.asStack());
        for (int i = 0; i < 3; i++) {
            scene.world().modifyBlock(util.grid().at(2, 1+i, 2), s -> s.setValue(CrucibleBlock.WINDOW, true), false);
            scene.idle(5);
        }

        // Show cogs and pipes
        scene.world().showSection(pipe, Direction.EAST);
        scene.idle(5);
        scene.world().showSection(largeCog1, Direction.NORTH);
        scene.world().showSection(shaft, Direction.DOWN);
        scene.idle(5);

        // Filling up multiple fluids
        List<FluidStack> fluids = List.of(new FluidStack(CMFluids.MOLTEN_GOLD.get().getSource(), 1000),
                new FluidStack(CMFluids.MOLTEN_IRON.get().getSource(), 1000),
                new FluidStack(CMFluids.MOLTEN_OBDURIUM.get().getSource(), 1000),
                new FluidStack(CMFluids.MOLTEN_SLAG.get().getSource(), 1000));

        for (FluidStack fluid : fluids) {
            scene.world().modifyBlockEntity(util.grid().at(1, 0, 6), FluidTankBlockEntity.class, be -> be.getTankInventory()
                    .fill(fluid, IFluidHandler.FluidAction.EXECUTE));
            scene.idle(20);
        }

        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .text("Crucibles can also store multiple fluids at the same time!")
                .pointAt(util.vector().blockSurface(crucibleController, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);
        scene.markAsFinished();
    }

    public static void foundry(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("foundry", "Setting up a Foundry");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos depotPos = util.grid().at(2, 1, 1);
        BlockPos funnelOutPos = depotPos.above();
        BlockPos tablePos = depotPos.east(2);
        BlockPos faucetPos = tablePos.above();
        BlockPos firstBurner = util.grid().at(2, 1, 2);
        BlockPos crucibleController = util.grid().at(2, 2, 2);
        BlockPos crucibleGauge = util.grid().at(3, 2, 2);

        Selection crucible = util.select().fromTo(2, 2, 2, 4, 4, 4);
        Selection burners = util.select().fromTo(2, 1, 3, 5, 1, 4)
                .add(util.select().fromTo(3, 1, 2, 4, 1, 2));
        Selection belt = util.select().fromTo(0, 1, 3, 1, 2, 3);

        Selection largeCog1 = util.select().position(2, 0, 6);
        Selection shaft = util.select().fromTo(1, 1, 4, 1, 1, 6);

        // Intro
        ElementLink<WorldSectionElement> crucibleElement = scene.world().showIndependentSection(crucible, Direction.DOWN);
        scene.world().moveSection(crucibleElement, util.vector().of(0, -1, 0), 0);
        scene.idle(10);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Industrial Crucibles can also be used as Foundries...")
                .pointAt(util.vector().blockSurface(crucibleController, Direction.WEST))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.NORTH), Pointing.RIGHT, 15).rightClick();
        scene.idle(7);
        scene.world().modifyBlockEntity(crucibleController, CrucibleBlockEntity.class, be -> be.foundry.setActive(true));

        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("...you can remove it any time using a Wrench")
                .pointAt(util.vector().blockSurface(crucibleGauge.below(), Direction.NORTH))
                .placeNearTarget();
        scene.idle(70);

        // Heat up Foundry
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.OUTPUT)
                .text("To melt metals, you will need to heat up the Foundry...")
                .pointAt(util.vector().blockSurface(crucibleGauge.below(), Direction.NORTH))
                .placeNearTarget();
        scene.idle(90);

        scene.world().moveSection(crucibleElement, util.vector().of(0, 1, 0), 10);
        scene.idle(10);
        scene.world().showIndependentSection(burners, Direction.NORTH);
        scene.world().showSection(util.select().position(firstBurner), Direction.NORTH);

        scene.overlay().showOutline(PonderPalette.RED, crucibleElement, crucible, 40);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.OUTPUT)
                .text("...Foundry can have a size up to 5x5 blocks in width to reach the maximum temperature")
                .pointAt(util.vector().blockSurface(crucibleGauge, Direction.NORTH))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.WEST), Pointing.LEFT, 15)
                .rightClick().withItem(AllItems.BLAZE_CAKE.asStack());
        scene.idle(10);
        burners.forEach(blockPos -> scene.world().modifyBlock(blockPos, s -> s.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), false));
        scene.world().modifyBlock(firstBurner, s -> s.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), false);
        scene.world().modifyBlockEntity(crucibleController, CrucibleBlockEntity.class, be -> be.foundry.updateTemperature(be));

        scene.idle(10);
        scene.world().hideSection(util.select().position(firstBurner), Direction.NORTH);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("However, each base block without a burner below it will cool down the Foundry. Higher heat means faster melting")
                .pointAt(util.vector().centerOf(firstBurner))
                .placeNearTarget();
        scene.idle(70);
        scene.world().showSection(util.select().position(firstBurner), Direction.SOUTH);

        // Melt down some blocks
        scene.idle(10);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.INPUT)
                .text("Unlike the Foundry Basin, this is large enough to melt down bigger items and several at once")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 3, 3), Direction.WEST))
                .placeNearTarget();
        scene.idle(40);

        ItemStack stack = CMBlocks.OBDURIUM_BLOCK.asStack();
        for (int i = 0; i < 9; i++) {
            ElementLink<EntityElement> item = scene.world().createItemEntity(util.vector().centerOf(3, 4, 3), util.vector().of(0, 0, 0), stack);
            scene.idle(5);
            int index = i;
            scene.world().modifyBlockEntity(crucibleController, CrucibleBlockEntity.class, be -> be.foundry.getInventory().insertItem(index, stack, false));
            scene.world().modifyEntity(item, Entity::discard);
        }

        scene.overlay().showControls(util.vector().blockSurface(crucibleGauge, Direction.NORTH), Pointing.DOWN, 60)
                .withItem(AllItems.GOGGLES.asStack());
        scene.idle(6);
        scene.overlay().showText(60)
                .text("The Foundry's current status can be inspected with Engineer's Goggles. The foundry's temperature is reported in Thermal Units (Tu)")
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .pointAt(util.vector().blockSurface(crucibleGauge, Direction.NORTH))
                .placeNearTarget();
        scene.idle(80);

        // Outputs/Inputs
        scene.world().showSection(largeCog1, Direction.NORTH);
        scene.world().showSection(shaft, Direction.DOWN);
        scene.idle(5);
        scene.world().showIndependentSection(belt, Direction.EAST);
        scene.idle(5);
        scene.world().showSection(util.select().position(depotPos), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(funnelOutPos), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(tablePos), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(faucetPos), Direction.SOUTH);
        scene.idle(10);

        int itemNumber = 9;
        for (int i = 0; i < itemNumber; i++) {
            int index = i;
            scene.world().modifyBlockEntity(crucibleController, CrucibleBlockEntity.class, be -> be.foundry.getInventory().setStackInSlot(index, ItemStack.EMPTY));
        }
        FluidStack fluid = new FluidStack(CMFluids.MOLTEN_OBDURIUM.get().getSource(), 810 * itemNumber);
        scene.world().modifyBlockEntity(crucibleController, CrucibleBlockEntity.class, be -> be.getTank().fill(fluid, IFluidHandler.FluidAction.EXECUTE));
        scene.idle(10);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Items can also be transferred on each side like an Item Vault")
                .pointAt(util.vector().blockSurface(util.grid().at(0, 1, 3), Direction.UP))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.FAST)
                .text("Foundries can also perform other types of recipes, such as entity melting and alloying")
                .pointAt(util.vector().blockSurface(crucibleController.above(), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);
        scene.markAsFinished();
    }
}