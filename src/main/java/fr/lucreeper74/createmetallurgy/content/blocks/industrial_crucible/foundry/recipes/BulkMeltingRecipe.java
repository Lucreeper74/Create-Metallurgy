package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.FoundryItemSlot;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.StandardFoundryRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.world.item.crafting.Recipe;

public class BulkMeltingRecipe extends StandardFoundryRecipe {

    public BulkMeltingRecipe(FoundryRecipeParams params) {
        super(CMRecipeTypes.BULK_MELTING, params);
    }

    public static boolean matches(CrucibleBlockEntity be, Recipe<?> recipe, FoundryItemSlot meltingSlot) {
        if (recipe instanceof ProcessingRecipe<?, ?> processingRecipe)
            return matchSpecific(be, meltingSlot.getStack(), processingRecipe) && FoundryRecipe.matchHeatCondition(be, processingRecipe);
        else
            return false;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 0;
    }
}