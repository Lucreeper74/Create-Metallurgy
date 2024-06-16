package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static com.simibubi.create.AllTags.forgeFluidTag;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMFluids {

    // Molten Iron
    public static final ResourceLocation MOLTEN_IRON_STILL_RL = CreateMetallurgy.genRL("fluid/iron/molten_iron_still");
    public static final ResourceLocation MOLTEN_IRON_FLOW_RL = CreateMetallurgy.genRL("fluid/iron/molten_iron_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_IRON = REGISTRATE.fluid("molten_iron", MOLTEN_IRON_STILL_RL, MOLTEN_IRON_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_iron"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Gold
    public static final ResourceLocation MOLTEN_GOLD_STILL_RL = CreateMetallurgy.genRL("fluid/gold/molten_gold_still");
    public static final ResourceLocation MOLTEN_GOLD_FLOW_RL = CreateMetallurgy.genRL("fluid/gold/molten_gold_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_GOLD = REGISTRATE.fluid("molten_gold", MOLTEN_GOLD_STILL_RL, MOLTEN_GOLD_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_gold"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Copper
    public static final ResourceLocation MOLTEN_COPPER_STILL_RL = CreateMetallurgy.genRL("fluid/copper/molten_copper_still");
    public static final ResourceLocation MOLTEN_COPPER_FLOW_RL = CreateMetallurgy.genRL("fluid/copper/molten_copper_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COPPER = REGISTRATE.fluid("molten_copper", MOLTEN_COPPER_STILL_RL, MOLTEN_COPPER_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_copper"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Zinc
    public static final ResourceLocation MOLTEN_ZINC_STILL_RL = CreateMetallurgy.genRL("fluid/zinc/molten_zinc_still");
    public static final ResourceLocation MOLTEN_ZINC_FLOW_RL = CreateMetallurgy.genRL("fluid/zinc/molten_zinc_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ZINC = REGISTRATE.fluid("molten_zinc", MOLTEN_ZINC_STILL_RL, MOLTEN_ZINC_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_zinc"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Brass
    public static final ResourceLocation MOLTEN_BRASS_STILL_RL = CreateMetallurgy.genRL("fluid/brass/molten_brass_still");
    public static final ResourceLocation MOLTEN_BRASS_FLOW_RL = CreateMetallurgy.genRL("fluid/brass/molten_brass_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRASS = REGISTRATE.fluid("molten_brass", MOLTEN_BRASS_STILL_RL, MOLTEN_BRASS_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_brass"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Tungsten
    public static final ResourceLocation MOLTEN_TUNGSTEN_STILL_RL = CreateMetallurgy.genRL("fluid/tungsten/molten_tungsten_still");
    public static final ResourceLocation MOLTEN_TUNGSTEN_FLOW_RL = CreateMetallurgy.genRL("fluid/tungsten/molten_tungsten_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TUNGSTEN = REGISTRATE.fluid("molten_tungsten", MOLTEN_TUNGSTEN_STILL_RL, MOLTEN_TUNGSTEN_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_tungsten"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Steel
    public static final ResourceLocation MOLTEN_STEEL_STILL_RL = CreateMetallurgy.genRL("fluid/steel/molten_steel_still");
    public static final ResourceLocation MOLTEN_STEEL_FLOW_RL = CreateMetallurgy.genRL("fluid/steel/molten_steel_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_STEEL = REGISTRATE.fluid("molten_steel", MOLTEN_STEEL_STILL_RL, MOLTEN_STEEL_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(forgeFluidTag("molten_steel"), forgeFluidTag("molten_materials"))
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    public static void register() {}
}
