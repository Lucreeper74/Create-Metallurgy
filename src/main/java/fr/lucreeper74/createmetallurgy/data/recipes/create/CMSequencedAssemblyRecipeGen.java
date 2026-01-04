package fr.lucreeper74.createmetallurgy.data.recipes.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.GrindingRecipe;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider.T;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public final class CMSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

    GeneratedRecipe

    INDUSTRIAL_CRUCIBLE = create("industrial_crucible", b -> b.require(Blocks.DEEPSLATE_BRICKS)
            .transitionTo(CMItems.INCOMPLETE_INDUSTRIAL_CRUCIBLE.get())
            .addOutput(CMBlocks.INDUSTRIAL_CRUCIBLE.get(), 1)
            .loops(1)
		    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.refractoryMortar()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.obduriumSheet()))
            .addStep(GrindingRecipe::new, rb -> rb.duration(80))
            .addStep(FillingRecipe::new, rb -> rb.require(CMFluids.MOLTEN_TUNGSTEN.get(), 30))
    ),

    STEEL_TRACK = create("steel_track", b -> b.require(T.sleepers())
            .transitionTo(AllItems.INCOMPLETE_TRACK.get())
            .addOutput(new ItemStack(AllBlocks.TRACK.get(), 12), 1)
            .loops(1)
		    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.steelIngot()))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.steelIngot()))
            .addStep(PressingRecipe::new, rb -> rb)
    ),

    LADLE = create("ladle", b -> {
                b.require(T.andesiteAlloy())
                        .transitionTo(CMItems.INCOMPLETE_LADLE_FRAME.get())
                        .loops(1)
                        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.steelIngot()))
                        .addStep(GrindingRecipe::new, rb -> rb.duration(40))
                        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(T.refractoryMortarBall()))
                        .addStep(PressingRecipe::new, rb -> rb);


                float rare_weight = 1f / LadleStyles.RARE_LADLES.size();
                float standard_weight = (LadleStyles.RARE_CHANCE - 1f) / LadleStyles.STANDARD_LADLES.size();

                for (LadleItem ladle : LadleStyles.ALL_LADLES)
                    b.addOutput(ladle, ladle.style.rare() ? rare_weight : standard_weight);

                return b;
            }
    )

    ;

    //

    public CMSequencedAssemblyRecipeGen(PackOutput output) {
        super(output, CreateMetallurgy.MOD_ID);
    }
}
