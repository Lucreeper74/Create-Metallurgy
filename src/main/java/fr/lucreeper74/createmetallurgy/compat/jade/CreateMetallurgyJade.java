package fr.lucreeper74.createmetallurgy.compat.jade;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.compat.jade.providers.LadleProvider;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CreateMetallurgyJade implements IWailaPlugin {

    private static final String ID = CreateMetallurgy.MOD_ID + ".jade_plugin";
    public static IWailaClientRegistration client;

    public static final ResourceLocation LADLE = ResourceLocation.fromNamespaceAndPath(ID, "ladle");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerFluidStorage(LadleProvider.INSTANCE, LadleEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerFluidStorageClient(LadleProvider.INSTANCE);

        TargetModifierLoader loader = new TargetModifierLoader();
        NeoForge.EVENT_BUS.addListener((TagsUpdatedEvent event) -> {
            if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
                loader.reload();
            }
        });
        registration.addRayTraceCallback(loader);
        registration.addTooltipCollectedCallback(loader);
    }
}