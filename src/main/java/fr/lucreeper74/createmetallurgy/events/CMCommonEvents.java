package fr.lucreeper74.createmetallurgy.events;

import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.BeltGrinderBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.basin.CastingBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.table.CastingTableBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin.FoundryBasinBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.labeling_station.LabelingStationBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.tundish.TundishBlockEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import fr.lucreeper74.createmetallurgy.registries.CMDataComponents;
import fr.lucreeper74.createmetallurgy.registries.CMEntityTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

@EventBusSubscriber
public class CMCommonEvents {

    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        LabelingStationBlockEntity.registerCapabilities(event);
        CrucibleBlockEntity.registerCapabilities(event);
        FoundryBasinBlockEntity.registerCapabilities(event);
        CastingBasinBlockEntity.registerCapabilities(event);
        CastingTableBlockEntity.registerCapabilities(event);
        BeltGrinderBlockEntity.registerCapabilities(event);
        TundishBlockEntity.registerCapabilities(event);

        // Items
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getClass() == LadleItem.class)
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) ->
                        new FluidHandlerItemStack(CMDataComponents.LADLE_FLUID_CONTENT, stack, LadleItem.getLadleCapacity()), item);
            if (item.getClass() == TagDependentBucketItem.class)
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item);
        }

        // Entity
        event.registerEntity(Capabilities.FluidHandler.ENTITY, CMEntityTypes.LADLE.get(), (entity, ctx) -> {
            if (entity instanceof LadleEntity ladleEntity)
                return new FluidHandlerItemStack(CMDataComponents.LADLE_FLUID_CONTENT, ladleEntity.box, LadleItem.getLadleCapacity());
            return null;
        });
    }
}
