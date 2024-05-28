package fr.lucreeper74.createmetallurgy.data;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMDamageTypes;
import fr.lucreeper74.createmetallurgy.worldgen.BiomeModifiers;
import fr.lucreeper74.createmetallurgy.worldgen.ConfiguredFeatures;
import fr.lucreeper74.createmetallurgy.worldgen.PlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CMGenEntriesProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifiers::bootstrap)
            .add(Registries.DAMAGE_TYPE, CMDamageTypes::bootstrap);

    public CMGenEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CreateMetallurgy.MOD_ID));
    }

    @Override
    public String getName() {
        return "Create Metallurgy's Generated Registry Entries";
    }
}