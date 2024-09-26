package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction;
import com.simibubi.create.foundation.utility.Pointing;
import fr.lucreeper74.createmetallurgy.content.foundry_mixer.FoundryMixerBlockEntity;
import fr.lucreeper74.createmetallurgy.content.foundry_lids.lid.FoundryLidBlockEntity;
import fr.lucreeper74.createmetallurgy.content.foundry_lids.glassed_lid.GlassedFoundryLidBlock;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
public class FoundryScenes {
    public static void foundryBasin(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("foundry_basin", "Melting Metals with Foundry Basin");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos burnerPos = util.grid.at(1, 1, 2);
        BlockPos drainPos = util.grid.at(1, 1, 1);
        BlockPos depotPos = util.grid.at(2, 1, 1);
        BlockPos basinPos = burnerPos.above();
        BlockPos lidPos = basinPos.above();

        Vec3 drainTop = util.vector.blockSurface(drainPos, Direction.UP);
        Vec3 lidSide = util.vector.blockSurface(lidPos, Direction.WEST);
        Vec3 lidTrap = util.vector.of(1.75, 3, 2.5);


        //Intro
        scene.world.showSection(util.select.position(burnerPos), Direction.DOWN);
        scene.idle(10);

        scene.world.modifyBlock(basinPos, s -> s.setValue(BasinBlock.FACING, Direction.DOWN), false);
        scene.world.showSection(util.select.position(basinPos), Direction.DOWN);
        scene.idle(10);
        Vec3 basinSide = util.vector.blockSurface(basinPos, Direction.WEST);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("The Foundry Basin is comparable to a classic Basin...")
                .pointAt(basinSide)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("...However, it can only hold 9 Items of the same type")
                .pointAt(basinSide)
                .placeNearTarget();
        scene.idle(90);

        scene.world.modifyBlock(lidPos, s -> s.setValue(GlassedFoundryLidBlock.OPEN, true), false);
        scene.world.showSection(util.select.position(lidPos), Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Put a Foundry Lid on top, and your Foundry is now ready!")
                .pointAt(lidSide)
                .placeNearTarget();
        scene.idle(90);


        //Show Faucet
        ElementLink<WorldSectionElement> depotLink =
                scene.world.showIndependentSection(util.select.position(depotPos), Direction.EAST);
        scene.world.moveSection(depotLink, util.vector.of(-1, 0, 0), 0);
        scene.idle(10);
        scene.world.modifyBlock(basinPos, s -> s.setValue(BasinBlock.FACING, Direction.NORTH), false);
        scene.idle(10);
        Vec3 faucet = basinSide.add(0.15, 0, -0.5);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("Like the Basin, a Faucet can extract recipes Items...")
                .pointAt(faucet)
                .placeNearTarget();
        scene.idle(90);


        //Show Item Drain
        scene.world.hideIndependentSection(depotLink, Direction.EAST);
        scene.idle(15);
        scene.world.showSection(util.select.position(drainPos), Direction.EAST);
        scene.idle(15);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("...and Fluids with a Item Drain")
                .pointAt(drainTop)
                .placeNearTarget();
        scene.idle(90);

        //Input Gold dusts & start Recipe
        scene.world.showSection(util.select.fromTo(2, 1, 2, 4, 1, 2), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 2, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 1, 3, 3, 1, 5), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        ItemStack goldDust = CMItems.GOLD_DUST.asStack();
        scene.world.createItemOnBelt(util.grid.at(4, 1, 2), Direction.UP, goldDust);
        scene.idle(40);
        scene.overlay.showControls(new InputWindowElement(basinSide, Pointing.LEFT)
                .withItem(CMItems.GOLD_DUST.asStack()), 20);
        scene.idle(30);

        //Closing Lid trap
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("Make sure to close the Foundry Top else the recipe won't start")
                .pointAt(lidTrap)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showControls(new InputWindowElement(lidTrap, Pointing.LEFT).rightClick(), 30);
        scene.idle(7);
        scene.world.modifyBlock(lidPos, s -> s.setValue(GlassedFoundryLidBlock.OPEN, false), false);

        //Start Recipe & Spoutout fluids
        scene.world.modifyBlockEntity(lidPos, FoundryLidBlockEntity.class, FoundryLidBlockEntity::startProcessingBasin);
        scene.idle(40);

        FluidStack gold = new FluidStack(FluidHelper.convertToStill(CMFluids.MOLTEN_GOLD.get()), 1000);
        scene.effects.emitParticles(util.vector.centerOf(drainPos),
                EmitParticlesInstruction.Emitter.withinBlockSpace(FluidFX.getFluidParticle(gold), Vec3.ZERO), 3, 20);
        scene.world.modifyBlockEntity(drainPos, ItemDrainBlockEntity.class, be -> {
            be.getBehaviour(SmartFluidTankBehaviour.TYPE)
                    .allowInsertion();
            be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .ifPresent(fh -> fh.fill(gold, IFluidHandler.FluidAction.EXECUTE));
        });
        scene.idle(10);
        scene.overlay.showControls(new InputWindowElement(drainTop, Pointing.RIGHT)
                .withItem(CMFluids.MOLTEN_GOLD.get().getFluidType().getBucket(gold)), 30);
        scene.idle(10);
    }

    public static void alloying(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("foundry_mixer", "Making Alloys with Foundry Mixer");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos burner = util.grid.at(1, 1, 2);
        BlockPos basinPos = burner.above();
        BlockPos topPos = burner.above(2);
        BlockPos tankPos = util.grid.at(0, 0, 5);
        BlockPos mixer = util.grid.at(1, 4, 2);
        BlockPos inputDepot = util.grid.at(4, 1, 1);

        Vec3 burnerSide = util.vector.blockSurface(burner, Direction.WEST);
        Vec3 mixerSide = util.vector.blockSurface(mixer, Direction.WEST);
        Vec3 basinSide = util.vector.blockSurface(basinPos, Direction.EAST);
        Vec3 lidTrap = util.vector.of(1.75, 3, 2.5);


        //Intro
        scene.world.showSection(util.select.position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 3, 1, 4, 3), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.modifyBlock(basinPos, s -> s.setValue(BasinBlock.FACING, Direction.DOWN), false);
        scene.world.showSection(util.select.position(basinPos), Direction.DOWN);
        scene.idle(5);
        scene.world.modifyBlock(topPos, s -> s.setValue(GlassedFoundryLidBlock.OPEN, true), false);
        scene.world.showSection(util.select.position(topPos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(mixer), Direction.DOWN);

        scene.idle(10);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("The Foundry Mixer is similar to the Mechanical Mixer...")
                .pointAt(mixerSide)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("...However, it has a much higher stress impact")
                .pointAt(mixerSide)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("With a Glassed Foundry Top between them, you will be able to make alloys")
                .pointAt(util.vector.blockSurface(topPos, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        //Blaze Burners texts
        scene.idle(10);
        scene.overlay.showText(80)
                .text("In order to keep molten metals warm enough...")
                .pointAt(burnerSide)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .text("...you will need a Super Heated Blaze Burner")
                .pointAt(burnerSide)
                .placeNearTarget();
        scene.idle(90);

        scene.overlay.showControls(new InputWindowElement(burnerSide, Pointing.LEFT).rightClick()
                .withItem(AllItems.BLAZE_CAKE.asStack()), 30);
        scene.idle(7);
        scene.world.modifyBlock(burner, s -> s.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SEETHING), false);
        scene.idle(20);

        //Adding Molten Iron
        scene.world.showSection(util.select.fromTo(0, 0, 5, 0, 3, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 2, 4), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 2, 2, 0, 2, 4), Direction.DOWN);
        scene.idle(10);
        FluidStack iron = new FluidStack(FluidHelper.convertToStill(CMFluids.MOLTEN_IRON.get()), 32000);

        scene.world.modifyBlockEntity(tankPos, FluidTankBlockEntity.class, be -> be.getTankInventory()
                .fill(iron, IFluidHandler.FluidAction.EXECUTE));
        scene.idle(5);
        scene.overlay.showControls(new InputWindowElement(basinSide, Pointing.RIGHT)
                .withItem(CMFluids.MOLTEN_IRON.get().getFluidType().getBucket(iron)), 30);
        scene.idle(40);

        //Closing Lid trap
        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .text("Make sure to close the Foundry Top else the recipe won't start")
                .pointAt(lidTrap)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showControls(new InputWindowElement(lidTrap, Pointing.LEFT).rightClick(), 30);
        scene.idle(7);
        scene.world.modifyBlock(topPos, s -> s.setValue(GlassedFoundryLidBlock.OPEN, false), false);
        scene.idle(40);

        scene.rotateCameraY(70);
        scene.idle(10);

        //Mechanical Arm to input Coke & Start Recipe
        BlockPos armPos = util.grid.at(3, 1, 3);
        scene.world.showSection(util.select.position(armPos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(inputDepot), Direction.DOWN);
        scene.idle(5);

        ItemStack coke = CMItems.COKE.asStack();

        scene.world.setKineticSpeed(util.select.position(armPos), -96);
        scene.idle(10);
        scene.world.createItemOnBeltLike(inputDepot, Direction.UP, coke);
        scene.idle(12);
        scene.world.instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(10);
        scene.world.removeItemsFromBelt(inputDepot);
        scene.world.instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, coke, 1);
        scene.idle(12);
        scene.world.instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, coke, 0);
        scene.idle(10);
        scene.world.instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, 1);
        scene.world.modifyBlockEntity(mixer, FoundryMixerBlockEntity.class, FoundryMixerBlockEntity::startProcessingBasin);
        scene.idle(40);

        //Show Pipes and Pump
        scene.world.showSection(util.select.position(0, 1, 3), Direction.EAST);
        scene.world.showSection(util.select.position(0, 1, 2), Direction.EAST);
        scene.world.showSection(util.select.position(0, 1, 1), Direction.EAST);
        scene.world.showSection(util.select.position(0, 1, 0), Direction.EAST);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 1, 0, 1, 2, 1), Direction.SOUTH);

        Vec3 smartPipeFilter = util.vector.topOf(util.grid.at(1, 2, 1))
                .subtract(0, 0, .25);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("If you are using Fluid Pipes instead of a Item Drain...")
                .pointAt(smartPipeFilter)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showFilterSlotInput(smartPipeFilter, Direction.NORTH, 80);
        scene.overlay.showText(80)
                .text("...you might use a Smart Fluid Pipe, to avoid pumping out un-processed Fluids")
                .pointAt(smartPipeFilter)
                .placeNearTarget();
        scene.idle(90);

        FluidStack steel = new FluidStack(FluidHelper.convertToStill(CMFluids.MOLTEN_STEEL.get()), 1000);
        ItemStack steelBucket = CMFluids.MOLTEN_STEEL.get().getFluidType().getBucket(steel);
        scene.overlay.showControls(new InputWindowElement(smartPipeFilter, Pointing.DOWN).rightClick()
                .withItem(steelBucket), 30);
        scene.idle(7);
        scene.world.setFilterData(util.select.position(1, 2, 1), SmartFluidPipeBlockEntity.class, steelBucket);
        scene.idle(10);

        scene.markAsFinished();
    }
}
