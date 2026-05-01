package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CMWashingRecipeGen extends WashingRecipeGen {

    public CMWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateMetallurgy.MOD_ID);
    }

    GeneratedRecipe
            COPPER_DUST = dirtyDust(CMItems.DIRTY_COPPER_DUST, CMItems.COPPER_DUST::get, () -> Items.CLAY_BALL, .5f),
            GOLD_DUST = dirtyDust(CMItems.DIRTY_GOLD_DUST, CMItems.GOLD_DUST::get, () -> Items.QUARTZ, .5f),
            IRON_DUST = dirtyDust(CMItems.DIRTY_IRON_DUST, CMItems.IRON_DUST::get, () -> Items.REDSTONE, .5f),
            ZINC_DUST = dirtyDust(CMItems.DIRTY_ZINC_DUST, CMItems.ZINC_DUST::get, () -> Items.GUNPOWDER, .5f),
            WOLFRAMITE_DUST = dirtyDust(CMItems.DIRTY_WOLFRAMITE_DUST, CMItems.WOLFRAMITE_DUST::get, () -> Items.GOLD_NUGGET, .5f);

    //

    public GeneratedRecipe dirtyDust(ItemEntry<TagDependentIngredientItem> dirtyDust, Supplier<ItemLike> dust, Supplier<ItemLike> secondary,
                                     float secondaryChance) {
        return create(dirtyDust::get, b -> b.output(dust.get())
                .output(secondaryChance, secondary.get()));
    }
}
