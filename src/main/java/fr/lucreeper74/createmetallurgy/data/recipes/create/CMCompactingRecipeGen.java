package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMCompactingRecipeGen extends CompactingRecipeGen {

    public CMCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            TUFF = create("tuff_from_slag", b -> b.require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(Items.GRAVEL)
            .require(Items.COBBLESTONE)
            .output(Blocks.TUFF, 1))

            ;
}