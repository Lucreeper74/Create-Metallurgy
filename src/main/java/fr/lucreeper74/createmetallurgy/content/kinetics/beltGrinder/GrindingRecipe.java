package fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class GrindingRecipe extends ProcessingRecipe<RecipeWrapper> {

	public GrindingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CMRecipeTypes.GRINDING, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }
}