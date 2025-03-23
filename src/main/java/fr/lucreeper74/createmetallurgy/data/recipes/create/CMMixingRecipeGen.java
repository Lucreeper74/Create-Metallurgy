package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

@SuppressWarnings("unused")
public class CMMixingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            REFRACTORY_MORTAR = create("refractory_mortar", b -> b.require(Tags.Items.SAND)
            .require(Tags.Items.SAND)
            .require(Items.CLAY_BALL)
            .require(Fluids.WATER, 100)
            .output(CMBlocks.REFRACTORY_MORTAR.get()));


    //

    public CMMixingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MIXING;
    }
}
