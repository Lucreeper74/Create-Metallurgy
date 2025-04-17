package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllRecipeTypes;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public class CMCompactingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            TUFF = create("tuff_from_slag", b -> b.require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(Items.GRAVEL)
            .require(Items.COBBLESTONE)
            .output(Blocks.TUFF, 1));


    //

    public CMCompactingRecipeGen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.COMPACTING;
    }
}