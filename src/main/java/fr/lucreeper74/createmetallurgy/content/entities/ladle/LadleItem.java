package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class LadleItem extends PackageItem {
    public static final int LADLE_CAPACITY = 9000; // in mb

    public LadleItem(Properties properties, PackageStyle style) {
        super(properties, style);
        
        PackageStyles.ALL_BOXES.remove(this); // Avoid touching Create's packages
        PackageStyles.STANDARD_BOXES.remove(this);

        LadleStyles.ALL_LADLES.add(this);
    }

    public static ItemStack containingFluid(List<FluidStack> fluidStacks) {
        FluidTank newTank = new FluidTank(LADLE_CAPACITY);
        fluidStacks.forEach(fluid -> newTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE));
        return containingFluid(newTank);
    }

    public static ItemStack containingFluid(FluidTank fluidTank) {
        ItemStack box = LadleStyles.getRandomBox();
        CompoundTag compound = new CompoundTag();
        compound.put("Fluids", fluidTank.writeToNBT(new CompoundTag()));
        box.setTag(compound);
        return box;
    }

    @Override
    public String getDescriptionId() {
        return "item." + CreateMetallurgy.MOD_ID + ".ladle";
    }

    @Override
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return LadleEntity.fromDroppedItem(world, location, itemstack);
    }
}