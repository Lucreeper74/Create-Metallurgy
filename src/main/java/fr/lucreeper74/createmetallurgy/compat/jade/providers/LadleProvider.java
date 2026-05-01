package fr.lucreeper74.createmetallurgy.compat.jade.providers;

import fr.lucreeper74.createmetallurgy.compat.jade.CreateMetallurgyJade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;
import snownee.jade.util.CommonProxy;

import java.util.List;

public enum LadleProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, FluidView> {
    INSTANCE;

    @Override
    public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, FluidView::readDefault, null);
    }

    @Override
    public @Nullable List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        return CommonProxy.wrapFluidStorage(accessor);
    }

    @Override
    public ResourceLocation getUid() {
        return CreateMetallurgyJade.LADLE;
    }
}