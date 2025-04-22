package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidSource;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.AllTags.forgeFluidTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class  CMFluids {
    public static final TagKey<Fluid> MOLTEN_MATERIALS = forgeFluidTag("molten_materials");

    public static final List<FluidEntry<ForgeFlowingFluid.Flowing>> ALL_MODDED_FLUIDS = new ArrayList<>();

    //Simple Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_IRON = moltenFluid("iron", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_GOLD = moltenFluid("gold", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COPPER = moltenFluid("copper", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ZINC = moltenFluid("zinc", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRASS = moltenFluid("brass", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TUNGSTEN = moltenFluid("tungsten", 2200, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_STEEL = moltenFluid("steel", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NETHERITE = moltenFluid("netherite", 2000, 1400, 10, 2, 25, 3, 100f);

    // Modded Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ALUMINUM = moddedMoltenFluid("aluminum", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LEAD = moddedMoltenFluid("lead", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NICKEL = moddedMoltenFluid("nickel", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OSMIUM = moddedMoltenFluid("osmium", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SILVER = moddedMoltenFluid("silver", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TIN = moddedMoltenFluid("tin", 2000, 1400, 10, 2, 25, 3, 100f);

    //Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OBDURIUM = moltenFluid("obdurium", 2400, 1400, 10, 2, 25, 3, 100f);

    // Modded Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_INVAR = moddedMoltenFluid("invar", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ELECTRUM = moddedMoltenFluid("electrum", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRONZE = moddedMoltenFluid("bronze", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_CONSTANTAN = moddedMoltenFluid("constantan", 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_VOID_STEEL = moddedMoltenFluid("void_steel", 2000, 1400, 10, 2, 25, 3, 100f);

    //Others
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SLAG = moltenFluid("slag", 2500, 1400, 12, 2, 25, 3, 100f);

    //

    private static FluidEntry<ForgeFlowingFluid.Flowing> moddedMoltenFluid(String name, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
        ResourceLocation STILL_RL = CreateMetallurgy.genRL("fluid/" + name + "/still");
        ResourceLocation FLOW_RL = CreateMetallurgy.genRL("fluid/" + name + "/flowing");
        FluidEntry<ForgeFlowingFluid.Flowing> entry = REGISTRATE.fluid("molten_" + name, STILL_RL, FLOW_RL, MoltenFluidType::new)
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
                .tag(forgeFluidTag("molten_" + name), MOLTEN_MATERIALS)
                .source(MoltenFluidSource::new)
                .bucket((content, props) -> new TagDependentBucketItem(content, props, AllTags.forgeItemTag("ingots/" + name)))
                .build()
                .register();

        ALL_MODDED_FLUIDS.add(entry);
        return entry;
    }

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
                .tag(forgeFluidTag("molten_" + name), MOLTEN_MATERIALS)
                .source(MoltenFluidSource::new)
                .bucket()
                .build()
                .register();
    }

    public static void register() {}
}
