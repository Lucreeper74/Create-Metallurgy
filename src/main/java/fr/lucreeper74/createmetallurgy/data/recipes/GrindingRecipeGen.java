package fr.lucreeper74.createmetallurgy.data.recipes;

import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class GrindingRecipeGen extends CMProcessingRecipesGen {


    GeneratedRecipe

            ALL_COPPER_BLOCKS = deoxidized(),
            ALL_WAXED_COPPER_BLOCKS = unwaxed()

            ;

    //

    protected GeneratedRecipe deoxidized() {
        for (Block current : WeatheringCopper.NEXT_BY_BLOCK.get().values()) {
            Block previous = WeatheringCopper.getPrevious(current).get();

            create(ForgeRegistries.BLOCKS.getKey(current).getPath(), b -> b.duration(50)
                    .require(current)
                    .output(previous));
        }
        return null;
    }
    protected GeneratedRecipe unwaxed() {
        Set<Block> coppers = new HashSet<>() {{
            addAll(WeatheringCopper.NEXT_BY_BLOCK.get().values());
            addAll(WeatheringCopper.PREVIOUS_BY_BLOCK.get().values());
        }};

        for (Block normal : coppers) {
            String waxedName = "waxed_" + ForgeRegistries.BLOCKS.getKey(normal).getPath();
            String modId = normal.toString().substring(6, normal.toString().indexOf(":"));
            Block waxed = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modId, waxedName));

            create(waxedName, b -> b.duration(50)
                    .require(waxed)
                    .output(normal));
        }
        return null;
    }

    //

    public GrindingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.GRINDING;
    }
}
