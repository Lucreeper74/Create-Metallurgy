package fr.lucreeper74.createmetallurgy.content.foundry_basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
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
        if (!optionalBasin.isPresent())
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
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (recipe == null)
            return false;
        Optional<BasinBlockEntity> basin = getBasin();
        if (!basin.isPresent())
            return false;
        return FoundryBasinRecipe.match((FoundryBasinBlockEntity) basin.get(), recipe);
    }
}
