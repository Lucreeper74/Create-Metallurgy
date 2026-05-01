package fr.lucreeper74.createmetallurgy.content.blocks.foundry_basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public abstract class FoundryBasinOperatingBE extends BasinOperatingBlockEntity {
    public FoundryBasinOperatingBE(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void applyBasinRecipe() {
        if (currentRecipe == null)
            return;

        Optional<BasinBlockEntity> optionalBasin = getBasin();
        if (optionalBasin.isEmpty())
            return;
        FoundryBasinBlockEntity basin = (FoundryBasinBlockEntity) optionalBasin.get();
        boolean wasEmpty = basin.canContinueProcessing();
        if (!FoundryBasinRecipe.apply(basin, currentRecipe))
            return;
        getProcessedRecipeTrigger().ifPresent(this::award);
        basin.inputTank.sendDataImmediately();

        // Continue mixing
        if (wasEmpty && matchBasinRecipe(currentRecipe)) {
            continueWithPreviousRecipe();
            sendData();
        }

        basin.notifyChangeOfContents();
    }

    @Override
    protected <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
        if (recipe == null)
            return false;
        return getBasin().filter(basinBlockEntity -> FoundryBasinRecipe.match(basinBlockEntity, recipe)).isPresent();
    }
}
