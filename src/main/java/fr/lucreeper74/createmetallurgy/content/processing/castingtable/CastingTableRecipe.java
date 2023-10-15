package fr.lucreeper74.createmetallurgy.content.processing.castingtable;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.SmartInventory;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;
import net.minecraft.world.level.Level;

public class CastingTableRecipe extends ProcessingRecipe<SmartInventory> {
    public CastingTableRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(AllRecipeTypes.CASTING_IN_TABLE, params);
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