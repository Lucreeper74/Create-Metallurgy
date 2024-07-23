package fr.lucreeper74.createmetallurgy.data.recipes;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.casting.recipe.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.DataGenerator;

import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class CastingRecipeGen extends CMRecipeProvider {

    /**
     * <pre><font color="orange">NAME</font> = create(<font color="orange">type</font>, "<font color="orange">name</font>", b -> b.require(<font color="orange">item</font>)
     *             .require(<font color="orange">fluid</font>, <font color="orange">amount</font>)
     *             .output(<font color="orange">item</font>)
     *             .withMoldConsumed() <font color="orange"><- Mold Condition (remove if false)</font>
     *             .duration(<font color="orange">time</font>));</pre>
     */

//    GeneratedRecipe
//
//            TUNGSTEN_SHEET = create(CMRecipeTypes.CASTING_IN_TABLE, "test1", b -> b.require(CMBlocks.COKE_BLOCK.get())
//            .require(CMFluids.MOLTEN_BRASS.get(), 250)
//            .output(CMItems.COPPER_DUST.get())
//            .withMoldConsumed()
//            .duration(50)),
//
//            TUNGSTEN_THING = create(CMRecipeTypes.CASTING_IN_BASIN, "test2", b -> b.require(CMBlocks.TUNGSTEN_BLOCK.get())
//            .require(CMFluids.MOLTEN_COPPER.get(), 250)
//            .output(CMItems.IRON_DUST.get())
//            .withMoldConsumed()
//            .duration(50))
//
//            ;

    public CastingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    protected CMRecipeProvider.GeneratedRecipe create(CMRecipeTypes type, String name, UnaryOperator<CastingRecipeBuilder> transform) {
        CMRecipeProvider.GeneratedRecipe generatedRecipe =
                c -> transform.apply(new CastingRecipeBuilder(type, CreateMetallurgy.genRL(name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    public String getName() {
        return "Create: Metallurgy's Casting Recipes";
    }
}