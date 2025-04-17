package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.Components;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.FoundryData;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FoundryDisplaySource extends DisplaySource {

    public static final List<MutableComponent> notEnoughSpaceSingle =
            List.of(CMLang.translateDirect("display_source.foundry.not_enough_space"),
                    CMLang.translateDirect("display_source.foundry.for_foundry_status"));

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (stats.maxRows() < 2)
            return notEnoughSpaceSingle;

        boolean isBook = context.getTargetBlockEntity() instanceof LecternBlockEntity;

        if (isBook) {
            Stream<MutableComponent> componentList = getComponents(context).map(components -> {
                Optional<MutableComponent> reduce = components.stream()
                        .reduce(MutableComponent::append);
                return reduce.orElse(EMPTY_LINE);
            });

            return List.of(componentList.reduce((comp1, comp2) -> comp1.append(Components.literal("\n"))
                            .append(comp2))
                    .orElse(EMPTY_LINE));
        }

        return getComponents(context).map(components -> {
                    Optional<MutableComponent> reduce = components.stream()
                            .reduce(MutableComponent::append);
                    return reduce.orElse(EMPTY_LINE);
                })
                .toList();
    }

    private Stream<List<MutableComponent>> getComponents(DisplayLinkContext context) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof CrucibleBlockEntity crucibleBE))
            return Stream.of(EMPTY);

        crucibleBE = crucibleBE.getControllerBE();
        if (crucibleBE == null)
            return Stream.of(EMPTY);

        FoundryData foundry = crucibleBE.foundry;
        foundry.updateTemperature(crucibleBE);

        return Stream.of(List.of(CMLang.translateDirect("foundry.status").append(":")),
                List.of(foundry.getHeatLevelComponent(crucibleBE.getBaseSize())));
    }

    @Override
    protected String getTranslationKey() {
        return "foundry_status";
    }
}
