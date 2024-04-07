package fr.lucreeper74.createmetallurgy.content.processing.casting.castingBasin;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.SmartInventory;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.world.level.Level;
public class CastingBasinRecipe extends ProcessingRecipe<SmartInventory> {
    public CastingBasinRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CMRecipeTypes.CASTING_IN_BASIN, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    public boolean matches(SmartInventory pContainer, Level pLevel) {
        return false;
    }
}