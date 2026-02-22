package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidSource;
import fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.ArrayList;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;

@SuppressWarnings("unused")
public class CMFluids {

    public static final List<FluidEntry<ForgeFlowingFluid.Flowing>> ALL_MOLTEN_FLUIDS = new ArrayList<>();
    public static final List<FluidEntry<ForgeFlowingFluid.Flowing>> ALL_MODDED_METALS = new ArrayList<>();

    //Simple Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_IRON = moltenMetalFluid(CMMetals.IRON, 1538, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_GOLD = moltenMetalFluid(CMMetals.GOLD, 1064, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_COPPER = moltenMetalFluid(CMMetals.COPPER, 1085, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ZINC = moltenMetalFluid(CMMetals.ZINC, 419, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRASS = moltenMetalFluid(CMMetals.BRASS, 932, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TUNGSTEN = moltenMetalFluid(CMMetals.TUNGSTEN, 3422, 2200, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_STEEL = moltenMetalFluid(CMMetals.STEEL, 1538, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NETHERITE = moltenMetalFluid(CMMetals.NETHERITE, 3524, 2000, 1400, 10, 2, 25, 3, 100f);

    // Modded Metals
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ALUMINUM = moltenMetalFluid(CMMetals.ALUMINUM, 660, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LEAD = moltenMetalFluid(CMMetals.LEAD, 327, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_NICKEL = moltenMetalFluid(CMMetals.NICKEL, 1455, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OSMIUM = moltenMetalFluid(CMMetals.OSMIUM, 3033, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SILVER = moltenMetalFluid(CMMetals.SILVER, 961, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_TIN = moltenMetalFluid(CMMetals.TIN, 231, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_LITHIUM = moltenMetalFluid(CMMetals.LITHIUM, 181, 2000, 1400, 10, 2, 25, 3, 100f);

    //Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_OBDURIUM = moltenMetalFluid(CMMetals.OBDURIUM, 3480, 2400, 1400, 10, 2, 25, 3, 100f);

    // Modded Alloys
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_INVAR = moltenMetalFluid(CMMetals.INVAR, 1425, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ELECTRUM = moltenMetalFluid(CMMetals.ELECTRUM, 996, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_BRONZE = moltenMetalFluid(CMMetals.BRONZE, 1000, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_CONSTANTAN = moltenMetalFluid(CMMetals.CONSTANTAN, 1221, 2000, 1400, 10, 2, 25, 3, 100f);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_VOID_STEEL = moltenMetalFluid(CMMetals.VOID_STEEL, 3635, 2000, 1400, 10, 2, 25, 3, 100f);

    //Others
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_SLAG = moltenFluid("slag", 1203, 2500, 1400, 12, 2, 25, 3, 100f);

    //

    private static FluidEntry<ForgeFlowingFluid.Flowing> moltenMetalFluid(CMMetals metal, int temperature, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
        String name = metal.getName();
        ResourceLocation STILL_RL = CreateMetallurgy.genRL("fluid/" + name + "/still");
        ResourceLocation FLOW_RL = CreateMetallurgy.genRL("fluid/" + name + "/flowing");
        FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> builder = REGISTRATE.fluid("molten_" + name, STILL_RL, FLOW_RL, MoltenFluidType::new)
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
                .source(MoltenFluidSource::new);

        if (!metal.isStandard())
            builder.bucket((content, props) -> new TagDependentBucketItem(content, props, metal.getItemTag(CMMetals.ItemType.INGOT)))
                    .onRegister(CMFluids::registerFluidDispenseBehavior).build();
        else
            builder.bucket()
                    .onRegister(CMFluids::registerFluidDispenseBehavior).build();

        FluidEntry<ForgeFlowingFluid.Flowing> entry = builder.register();

        if (!metal.isStandard())
            ALL_MODDED_METALS.add(entry);
        ALL_MOLTEN_FLUIDS.add(entry);
        return entry;
    }

    private static FluidEntry<ForgeFlowingFluid.Flowing> moltenFluid(String name, int temperature, int viscosity, int density, int lightLevel, int levelDecrease, int tickRate, int slopeDistance, float explosionResistance) {
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
                .bucket()
                .onRegister(CMFluids::registerFluidDispenseBehavior)
                .build()
                .register();

        ALL_MOLTEN_FLUIDS.add(entry);
        return entry;
    }

    @Deprecated
    public static boolean isMoltenMaterial(Fluid fluid) {
        return fluid.is(CMTags.CMFluidTags.MOLTEN_MATERIAL.tag);
    }

    public static void register() {
    }

    public static void registerFluidInteractions() {
        for (FluidEntry<ForgeFlowingFluid.Flowing> fluidEntry : ALL_MOLTEN_FLUIDS) {
            FluidInteractionRegistry.addInteraction(fluidEntry.getType(), new FluidInteractionRegistry.InteractionInformation(ForgeMod.WATER_TYPE.get(),
                    fluidState -> {
                        if (fluidState.isSource())
                            return CMBlocks.SLAG_BLOCK.get().defaultBlockState();
                        else
                            return Blocks.COBBLESTONE.defaultBlockState();
                    }
            ));
        }
    }

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.getPos().relative(pSource.getBlockState().getValue(DispenserBlock.FACING));
            Level level = pSource.getLevel();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null, pStack)) {
                return new ItemStack(Items.BUCKET);
            }
            return DEFAULT.dispense(pSource, pStack);
        }
    };

    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
    }
}
