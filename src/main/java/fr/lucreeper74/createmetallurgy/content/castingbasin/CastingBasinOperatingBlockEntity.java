package fr.lucreeper74.createmetallurgy.content.castingbasin;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.AllRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CastingBasinOperatingBlockEntity extends BasinOperatingBlockEntity {

    public int processingTime;
    public boolean running;
    public CastingBasinOperatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    protected void onBasinRemoved() {
        if (!this.running) return;
        this.processingTime = 0;
        this.currentRecipe = null;
        this.running = false;
    }


    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == AllRecipeTypes.MELTING.getType();
    }

    private static final Object CastingBasinMeltingRecipesKey = new Object();
    @Override
    protected Object getRecipeCacheKey() {
        return CastingBasinMeltingRecipesKey;
    }
}
