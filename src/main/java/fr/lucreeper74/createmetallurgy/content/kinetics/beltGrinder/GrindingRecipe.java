package fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.Lang;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class GrindingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

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

    @Override
    public void addAssemblyIngredients(List< Ingredient > list) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        //TODO CHANGE THIS
        return Lang.translateDirect("recipe.assembly.cutting");
    }

    @Override
    public void addRequiredMachines(Set< ItemLike > list) {
        list.add(CMBlocks.BELT_GRINDER_BLOCK.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblyCutting::new;
    }

}