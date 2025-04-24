package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleEntity;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleRenderer;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleVisual;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

public class CMEntityTypes {

    public static final EntityEntry<LadleEntity> LADLE = register("ladle", LadleEntity::new, () -> LadleRenderer::new,
            MobCategory.MISC, 10, 3, true, false, LadleEntity::build)
            .visual(() -> LadleVisual::new, true)
            .register();

    //

    private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityType.EntityFactory<T> factory,
                                                                         NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                         MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                         NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = Lang.asId(name);
        return (CreateEntityBuilder<T, ?>) CreateMetallurgy.REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(LADLE.get(), PackageEntity.createPackageAttributes()
                .build());
    }

    public static void register() {}
}