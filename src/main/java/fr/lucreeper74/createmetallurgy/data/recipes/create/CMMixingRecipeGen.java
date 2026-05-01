package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CMMixingRecipeGen extends MixingRecipeGen {

    public CMMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe

            REFRACTORY_MORTAR = create("refractory_mortar", b -> b.require(Tags.Items.SANDS)
            .require(Tags.Items.SANDS)
            .require(Items.CLAY_BALL)
            .require(Fluids.WATER, 100)
            .output(CMBlocks.REFRACTORY_MORTAR.get()));
}
