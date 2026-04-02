package fr.lucreeper74.createmetallurgy.compat.jade.providers;

import fr.lucreeper74.createmetallurgy.compat.jade.CreateMetallurgyJade;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;
import snownee.jade.util.JadeForgeUtils;

import java.util.List;

public enum LadleProvider implements IServerExtensionProvider<LadleEntity, CompoundTag>, IClientExtensionProvider<CompoundTag, FluidView> {
    INSTANCE;

    @Override
    public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, FluidView::readDefault, null);
    }

    @Override
    public List<ViewGroup<CompoundTag>> getGroups(
            ServerPlayer player,
            ServerLevel level,
            LadleEntity entity,
            boolean showDetails) {
        return JadeForgeUtils.fromFluidHandler(LadleItem.getFluidContents(entity.getBox()));
    }

    @Override
    public ResourceLocation getUid() {
        return CreateMetallurgyJade.LADLE;
    }
}