package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.LightBulbBlock;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;
import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;

public class CMCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(CREATIVE_MODE_TAB, CreateMetallurgy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_CREATIVE_TAB = REGISTER.register(CreateMetallurgy.MOD_ID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + CreateMetallurgy.MOD_ID + ".main_group"))
                    .icon(CMItems.OBDURIUM_INGOT::asStack)
                    .displayItems((itemDisplayParameters, output) -> REGISTRATE.getAll(Registries.ITEM).forEach((entry -> {
                        if (!CreateRegistrate.isInCreativeTab(entry, CMCreativeTabs.MAIN_CREATIVE_TAB))
                            return;

                        Item item = entry.get();
                        if (makeExclusionPredicate().test(item))
                            return;

                        output.accept(item, makeVisibilityFunc().apply(item));
                    })))
                    .build());

    private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
        Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

        for (BlockEntry<LightBulbBlock> entry : CMBlocks.LIGHT_BULBS) {
            LightBulbBlock block = entry.get();
            if (block.getColor() != DyeColor.WHITE) {
                visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
            }
        }

        return item -> {
            CreativeModeTab.TabVisibility visibility = visibilities.get(item);
            if (visibility != null) {
                return visibility;
            }
            return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        };
    }

    private static Predicate<Item> makeExclusionPredicate() {
        Set<Item> exclusions = new ReferenceOpenHashSet<>();

        List<ItemProviderEntry<?, ?>> simpleExclusions = List.of(
                CMItems.INCOMPLETE_INDUSTRIAL_CRUCIBLE,
                CMItems.INCOMPLETE_LADLE_FRAME
        );

        List<TagDependentBucketItem> tagDependentExclusions = CMFluids.ALL_MODDED_METALS.stream()
                .map(entry -> (TagDependentBucketItem) entry.getBucket().get())
                .toList();

        for (ItemProviderEntry<?, ?> entry : simpleExclusions) {
            exclusions.add(entry.asItem());
        }

        for (TagDependentBucketItem bucketItem : tagDependentExclusions) {
            if (bucketItem.shouldHide()) {
                exclusions.add(bucketItem);
            }
        }

        exclusions.addAll(LadleStyles.RARE_LADLES);

        return exclusions::contains;
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}