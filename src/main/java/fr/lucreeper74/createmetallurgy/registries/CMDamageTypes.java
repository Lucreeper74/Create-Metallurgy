package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class CMDamageTypes {

    //Damage Types -----------------------------------------------------------------------------
    public static final ResourceKey<DamageType>
            GRINDER = key("mechanical_grinder"),
            MOLTEN_FLUID = key("molten_fluid"),
            FOUNDRY = key("crucible");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreateMetallurgy.genRL(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new DamageTypeBuilder(GRINDER).register(ctx);
        new DamageTypeBuilder(MOLTEN_FLUID).scaling(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER).effects(DamageEffects.BURNING).exhaustion(.2f).register(ctx);
        new DamageTypeBuilder(FOUNDRY).scaling(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER).effects(DamageEffects.BURNING).exhaustion(.1f).register(ctx);
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key));
    }

    // Damage Sources --------------------------------------------------------------------------
    public static DamageSource grinder(Level level) {
        return source(GRINDER, level);
    }

    public static DamageSource moltenFluid(Level level) {
        return source(MOLTEN_FLUID, level);
    }

    public static DamageSource foundry(Level level) {
        return source(FOUNDRY, level);
    }
}
