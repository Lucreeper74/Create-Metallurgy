package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.simibubi.create.AllTags.forgeFluidTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMFluids {
    //Simple Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_IRON = moltenFluid("iron", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_GOLD = moltenFluid("gold", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COPPER = moltenFluid("copper", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ZINC = moltenFluid("zinc", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRASS = moltenFluid("brass", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TUNGSTEN = moltenFluid("tungsten", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_STEEL = moltenFluid("steel", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NETHERITE = moltenFluid("netherite", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ALUMINUM = moltenFluid("aluminum", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LEAD = moltenFluid("lead", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NICKEL = moltenFluid("nickel", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OSMIUM = moltenFluid("osmium", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SILVER = moltenFluid("silver", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TIN = moltenFluid("tin", 2000, 1400, 10, 2, 25, 3, 100f);

    //Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_INVAR = moltenFluid("invar", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ELECTRUM = moltenFluid("electrum", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRONZE = moltenFluid("bronze", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_CONSTANTAN = moltenFluid("constantan", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_VOID_STEEL = moltenFluid("void_steel", 2000, 1400, 10, 2, 25, 3, 100f);

    //

    private static FluidEntry<ForgeFlowingFluid.Flowing> moltenFluid(String name, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
        ResourceLocation STILL_RL = CreateMetallurgy.genRL("fluid/" + name + "/still");
        ResourceLocation FLOW_RL = CreateMetallurgy.genRL("fluid/" + name + "/flowing");
        return REGISTRATE.fluid("molten_" + name, STILL_RL, FLOW_RL, MoltenFluidType::new)
                .properties(b -> b.viscosity(viscosity)
                        .density(density)
                        .lightLevel(lightLevel)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                        .canHydrate(false).canDrown(false).canSwim(false))
                .fluidProperties(p -> p.levelDecreasePerBlock(levelDecrease)
                        .tickRate(tickRate)
                        .slopeFindDistance(slopeDistance)
                        .explosionResistance(explosionResistance))
                .tag(forgeFluidTag("molten_" + name), forgeFluidTag("molten_materials"))
                .source(ForgeFlowingFluid.Source::new)
                .bucket()
                .build()
                .register();
    }

    public static void register() {}
}
