package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.data.recipes.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CMCrushingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

            NETHER_WOLFRAMITE_ORE = netherOre(CMBlocks.WOLFRAMITE_ORE::get, CMItems.CRUSHED_RAW_WOLFRAMITE::get, 1, 350),
            RAW_WOLFRAMITE = rawOre("wolframite", T::tungstenBlock, CMItems.CRUSHED_RAW_WOLFRAMITE::get, 1),
            RAW_WOLFRAMITE_BLOCK = rawOreBlock("wolframite", T::wolframiteBlock, CMItems.CRUSHED_RAW_WOLFRAMITE::get, 1)

    ;

    //

    protected GeneratedRecipe netherOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount,
                                                             int duration) {
        return ore(Blocks.NETHERRACK, ore, raw, expectedAmount, duration);
    }

    protected GeneratedRecipe ore(ItemLike stoneType, Supplier<ItemLike> ore, Supplier<ItemLike> raw,
                                                       float expectedAmount, int duration) {
        return create(ore, b -> {
            ProcessingRecipeBuilder<ProcessingRecipe<?>> builder = b.duration(duration)
                    .output(raw.get(), Mth.floor(expectedAmount));
            float extra = expectedAmount - Mth.floor(expectedAmount);
            if (extra > 0)
                builder.output(extra, raw.get(), 1);
            builder.output(.75f, AllItems.EXP_NUGGET.get(), raw.get() == AllItems.CRUSHED_GOLD.get() ? 2 : 1);
            return builder.output(.125f, stoneType);
        });
    }

    protected GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
        return rawOre(metalName, input, result, false, xpMult);
    }

    protected GeneratedRecipe rawOreBlock(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
        return rawOre(metalName, input, result, true, xpMult);
    }

    protected GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, boolean block, int xpMult) {
        return create("raw_" + metalName + (block ? "_block" : ""), b -> {
            int amount = block ? 9 : 1;
            return b.duration(400)
                    .require(input.get())
                    .output(result.get(), amount)
                    .output(.75f, AllItems.EXP_NUGGET.get(), amount * xpMult);
        });
    }

    //

    public CMCrushingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.CRUSHING;
    }
}
