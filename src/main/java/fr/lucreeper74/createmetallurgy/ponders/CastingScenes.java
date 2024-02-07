package fr.lucreeper74.createmetallurgy.ponders;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingbasin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.processing.casting.castingtable.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.AllFluids;
import fr.lucreeper74.createmetallurgy.registries.AllItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CastingScenes {
    public static void castingBlocks(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("casting_blocks", "Casting Molten Metals");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos basinPos = util.grid.at(3, 1, 2);
        BlockPos tablePos = util.grid.at(1, 1, 2);
        BlockPos tankPos = util.grid.at(2, 0, 5);
        BlockPos leverPos = util.grid.at(2, 1, 1);
        BlockPos tableFunnelPos = tablePos.north();
        BlockPos basinFunnelPos = basinPos.north();

        Selection basin = util.select.position(basinPos);
        Selection table = util.select.position(tablePos);
        Selection funnelBasin = util.select.position(basinPos.north());
        Selection funnelTable = util.select.position(tablePos.north());

        Vec3 tableTop = util.vector.topOf(tablePos);
        Vec3 basinTop = util.vector.topOf(basinPos);

        //Intro
        scene.world.showSection(util.select.fromTo(2, 0, 5, 2, 2, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 1, 4), Direction.DOWN);
        scene.world.showSection(util.select.fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(3, 1, 4), Direction.NORTH);
        scene.world.showSection(util.select.fromTo(3, 0, 5, 3, 1, 5), Direction.NORTH);
        scene.idle(5);
        scene.world.showSection(basin, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(table, Direction.DOWN);
        scene.idle(10);

        scene.overlay.showOutline(PonderPalette.WHITE, new Object(), basin, 80);
        scene.overlay.showOutline(PonderPalette.WHITE, new Object(), table, 80);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("Casting Blocks can be used to cool down Molten Metals into a specific shape")
                .pointAt(tableTop)
                .placeNearTarget();
        scene.idle(90);

        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("The Casting Basin is used to cast up to a entire block")
                .pointAt(basinTop)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("While the Casting Table is used to cast Items using Graphite Molds")
                .pointAt(tableTop)
                .placeNearTarget();
        scene.idle(90);


        //Add Ingot Mold
        scene.overlay.showControls(new InputWindowElement(tableTop, Pointing.LEFT).rightClick()
                .withItem(AllItems.GRAPHITE_INGOT_MOLD.asStack()), 30);
        scene.idle(7);
        scene.world.modifyBlockEntity(tablePos, CastingTableBlockEntity.class, be ->
                be.moldInv.insertItem(0, AllItems.GRAPHITE_INGOT_MOLD.asStack(), false));
        scene.idle(20);

        //Add Brass & start recipes
        FluidStack brass = new FluidStack(FluidHelper.convertToStill(AllFluids.MOLTEN_BRASS.get()), 24000);
        ItemStack ingot = com.simibubi.create.AllItems.BRASS_INGOT.asStack();
        ItemStack block = AllBlocks.BRASS_BLOCK.asStack();

        scene.world.modifyBlockEntity(tankPos, FluidTankBlockEntity.class, be -> be.getTankInventory()
                .fill(brass, IFluidHandler.FluidAction.EXECUTE));
        scene.idle(40);
        scene.world.modifyBlockEntity(tablePos, CastingTableBlockEntity.class, CastingTableBlockEntity::startProcess);
        scene.world.modifyBlockEntity(basinPos, CastingBasinBlockEntity.class, CastingBasinBlockEntity::startProcess);

        //Add Output & stop recipes
        scene.idle(60);
        scene.world.modifyBlockEntity(tablePos, CastingTableBlockEntity.class, be -> be.inv
                .setStackInSlot(0, ingot));
        scene.world.modifyBlockEntity(tablePos, CastingTableBlockEntity.class, CastingTableBlockEntity::processFailed);

        scene.world.modifyBlockEntity(basinPos, CastingBasinBlockEntity.class, be -> be.inv
                .setStackInSlot(0, block));
        scene.world.modifyBlockEntity(basinPos, CastingBasinBlockEntity.class, CastingBasinBlockEntity::processFailed);
        scene.idle(5);

        scene.overlay.showControls(new InputWindowElement(tableTop, Pointing.DOWN)
                .withItem(ingot), 30);
        scene.overlay.showControls(new InputWindowElement(basinTop, Pointing.DOWN)
                .withItem(block), 30);
        scene.idle(20);

        //Add Funnels
        scene.world.showSection(util.select.position(2, 1, 1), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(funnelBasin, Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(funnelTable, Direction.SOUTH);
        scene.idle(10);

        //Show Funnel Filter
        Vec3 filter = util.vector.topOf(tableFunnelPos).add(0, -5 / 16f, -1.5 / 16f);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("If you are using Funnels instead of manually...")
                .pointAt(tableTop)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showFilterSlotInput(filter, Direction.NORTH, 80);
        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("...you might need a Brass Funnel to not extract Molds")
                .pointAt(filter)
                .placeNearTarget();
        scene.idle(90);
        scene.overlay.showControls(new InputWindowElement(filter, Pointing.LEFT).rightClick()
                .withItem(ingot), 30);
        scene.idle(7);
        scene.world.setFilterData(funnelTable, FunnelBlockEntity.class, ingot);
        scene.idle(5);

        //Power off funnels
        scene.effects.indicateRedstone(leverPos);
        scene.world.modifyBlock(leverPos, s -> s.cycle(LeverBlock.POWERED), false);
        scene.world.modifyBlock(basinFunnelPos, s -> s.cycle(FunnelBlock.POWERED), false);
        scene.world.modifyBlock(tableFunnelPos, s -> s.cycle(FunnelBlock.POWERED), false);

        //Extract Items
        scene.world.flapFunnel(basinFunnelPos, true);
        scene.world.createItemEntity(util.vector.centerOf(basinFunnelPos), util.vector.of(0, 0, 0), block);
        scene.world.modifyBlockEntity(basinPos, CastingBasinBlockEntity.class, be -> be.inv
                .clearContent());
        scene.world.flapFunnel(tableFunnelPos, true);
        scene.world.createItemEntity(util.vector.centerOf(tableFunnelPos), util.vector.of(0, 0, 0), ingot);
        scene.world.modifyBlockEntity(tablePos, CastingTableBlockEntity.class, be -> be.inv
                .clearContent());
    }
}