package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidSource;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.ArrayList;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class  CMFluids {

    public static final List<FluidEntry<ForgeFlowingFluid.Flowing>> ALL_MODDED_FLUIDS = new ArrayList<>();

    //Simple Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_IRON = moltenFluid(CMMetals.IRON.getName(), 1538, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_GOLD = moltenFluid(CMMetals.GOLD.getName(), 1064, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COPPER = moltenFluid(CMMetals.COPPER.getName(), 1085, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ZINC = moltenFluid(CMMetals.ZINC.getName(), 419, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRASS = moltenFluid(CMMetals.BRASS.getName(), 932, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TUNGSTEN = moltenFluid(CMMetals.TUNGSTEN.getName(), 3422, 2200, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_STEEL = moltenFluid(CMMetals.STEEL.getName(), 1538, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NETHERITE = moltenFluid(CMMetals.NETHERITE.getName(), 3524, 2000, 1400, 10, 2, 25, 3, 100f);

    // Modded Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ALUMINUM = moddedMoltenFluid(CMMetals.ALUMINUM, 660, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LEAD = moddedMoltenFluid(CMMetals.LEAD, 327, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NICKEL = moddedMoltenFluid(CMMetals.NICKEL, 1455, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OSMIUM = moddedMoltenFluid(CMMetals.OSMIUM, 3033, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SILVER = moddedMoltenFluid(CMMetals.SILVER, 961, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TIN = moddedMoltenFluid(CMMetals.TIN, 231, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LITHIUM = moddedMoltenFluid(CMMetals.LITHIUM, 181, 2000, 1400, 10, 2, 25, 3, 100f);

    //Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OBDURIUM = moltenFluid(CMMetals.OBDURIUM.getName(), 3480, 2400, 1400, 10, 2, 25, 3, 100f);

    // Modded Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_INVAR = moddedMoltenFluid(CMMetals.INVAR, 1425, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ELECTRUM = moddedMoltenFluid(CMMetals.ELECTRUM, 996, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRONZE = moddedMoltenFluid(CMMetals.BRONZE, 1000, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_CONSTANTAN = moddedMoltenFluid(CMMetals.CONSTANTAN, 1221, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_VOID_STEEL = moddedMoltenFluid(CMMetals.VOID_STEEL, 3635, 2000, 1400, 10, 2, 25, 3, 100f);

    //Others
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SLAG = moltenFluid("slag", 1203, 2500, 1400, 12, 2, 25, 3, 100f);

    //

    private static FluidEntry<ForgeFlowingFluid.Flowing> moddedMoltenFluid(CMMetals metal, int temperature, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
        String name = metal.getName();
        ResourceLocation STILL_RL = CreateMetallurgy.genRL("fluid/" + name + "/still");
        ResourceLocation FLOW_RL = CreateMetallurgy.genRL("fluid/" + name + "/flowing");
        FluidEntry<ForgeFlowingFluid.Flowing> entry = REGISTRATE.fluid("molten_" + name, STILL_RL, FLOW_RL, MoltenFluidType::new)
                .properties(b -> b.temperature(temperature)
                        .viscosity(viscosity)
                        .density(density)
                        .lightLevel(lightLevel)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                        .canHydrate(false).canDrown(false).canSwim(false))
                .fluidProperties(p -> p.levelDecreasePerBlock(levelDecrease)
                        .tickRate(tickRate)
                        .slopeFindDistance(slopeDistance)
                        .explosionResistance(explosionResistance))
                .tag(CMTags.CMFluidTags.MOLTEN_MATERIAL.tag, AllTags.AllFluidTags.BOTTOMLESS_DENY.tag)
                .source(MoltenFluidSource::new)
                .bucket((content, props) -> new TagDependentBucketItem(content, props, metal.getItemTag(CMMetals.ItemType.INGOT)))
                .build()
                .register();

        ALL_MODDED_FLUIDS.add(entry);
        return entry;
    }

    private static FluidEntry<ForgeFlowingFluid.Flowing> moltenFluid(String name, int temperature, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
        ResourceLocation STILL_RL = CreateMetallurgy.genRL("fluid/" + name + "/still");
        ResourceLocation FLOW_RL = CreateMetallurgy.genRL("fluid/" + name + "/flowing");
        return REGISTRATE.fluid("molten_" + name, STILL_RL, FLOW_RL, MoltenFluidType::new)
                .properties(b -> b.temperature(temperature)
                        .viscosity(viscosity)
                        .density(density)
                        .lightLevel(lightLevel)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                        .canHydrate(false).canDrown(false).canSwim(false))
                .fluidProperties(p -> p.levelDecreasePerBlock(levelDecrease)
                        .tickRate(tickRate)
                        .slopeFindDistance(slopeDistance)
                        .explosionResistance(explosionResistance))
                .tag(CMTags.CMFluidTags.MOLTEN_MATERIAL.tag, AllTags.AllFluidTags.BOTTOMLESS_DENY.tag)
                .source(MoltenFluidSource::new)
                .bucket()
                .build()
                .register();
    }

    @Deprecated
    public static boolean isMoltenMaterial(Fluid fluid) {
        return fluid.is(CMTags.CMFluidTags.MOLTEN_MATERIAL.tag) || fluid.is(FluidTags.LAVA);
    }

    public static void register() {}
}
