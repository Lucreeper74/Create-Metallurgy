package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllDamageTypes;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;

public class CMDamageTypes {

    //Damage Types -----------------------------------------------------------------------------
    public static final ResourceKey<DamageType>
            GRINDER = key("mechanical_grinder");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreateMetallurgy.genRL(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new DamageTypeBuilder(GRINDER).register(ctx);
    }

    // Damage Sources --------------------------------------------------------------------------
    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key));
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity entity) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key), entity);
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key), causingEntity, directEntity);
    }

    public static DamageSource grinder(Level level) {
        return source(GRINDER, level);
    }
}
