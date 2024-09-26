package fr.lucreeper74.createmetallurgy.compat;

import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public enum CMCompatMetals {
    //Simple metals
    ALUMINUM(CMFluids.MOLTEN_ALUMINUM),
    LEAD(CMFluids.MOLTEN_LEAD),
    NICKEL(CMFluids.MOLTEN_NICKEL),
    OSMIUM(CMFluids.MOLTEN_OSMIUM),
    SILVER(CMFluids.MOLTEN_SILVER),
    TIN(CMFluids.MOLTEN_TIN),

    //Alloys
    INVAR(CMFluids.MOLTEN_INVAR),
    ELECTRUM(CMFluids.MOLTEN_ELECTRUM),
    BRONZE(CMFluids.MOLTEN_BRONZE),
    CONSTANTAN(CMFluids.MOLTEN_CONSTANTAN),
    VOID_STEEL(CMFluids.MOLTEN_VOID_STEEL);

    private final String name;
    private final FluidEntry<ForgeFlowingFluid.Flowing> fluid;

    CMCompatMetals(FluidEntry<ForgeFlowingFluid.Flowing> fluid) {
        this.name = CMLang.asId(name());
        this.fluid = fluid;
    }

    public String getName() {
        return name;
    }

    public FluidEntry<ForgeFlowingFluid.Flowing> getFluid() {
        return fluid;
    }
}
