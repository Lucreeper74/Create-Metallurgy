package fr.lucreeper74.createmetallurgy.registries;

import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class AllFluids {

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
            .tag(FluidTags.LAVA)
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
            .tag(FluidTags.LAVA)
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
            .tag(FluidTags.LAVA)
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
            .tag(FluidTags.LAVA)
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
            .tag(FluidTags.LAVA)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    // Molten Cobalt
    public static final ResourceLocation MOLTEN_COBALT_STILL_RL = CreateMetallurgy.genRL("fluid/cobalt/molten_cobalt_still");
    public static final ResourceLocation MOLTEN_COBALT_FLOW_RL = CreateMetallurgy.genRL("fluid/cobalt/molten_cobalt_flow");
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COBALT = REGISTRATE.fluid("molten_cobalt", MOLTEN_COBALT_STILL_RL, MOLTEN_COBALT_FLOW_RL)
            .properties(b -> b.viscosity(2000)
                    .density(1400)
                    .lightLevel(10)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(FluidTags.LAVA)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    public static void register() {}
}
