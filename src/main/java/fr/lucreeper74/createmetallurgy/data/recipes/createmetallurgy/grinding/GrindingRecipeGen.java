package fr.lucreeper74.createmetallurgy.data.recipes.createmetallurgy.grinding;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.GrindingRecipe;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GrindingRecipeGen extends StandardProcessingRecipeGen<GrindingRecipe> {

    public GrindingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected GeneratedRecipe deoxidized() {
        for (Block current : WeatheringCopper.NEXT_BY_BLOCK.get().values()) {
            Block previous = WeatheringCopper.getPrevious(current).get();

            create(BuiltInRegistries.BLOCK.getKey(current).getPath(), b -> b.duration(50)
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
            String waxedName = "waxed_" + BuiltInRegistries.BLOCK.getKey(normal).getPath();
            String modId = normal.toString().substring(6, normal.toString().indexOf(":"));
            Block waxed = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modId, waxedName));

            create(waxedName, b -> b.duration(50)
                    .require(waxed)
                    .output(normal));
        }
        return null;
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.GRINDING;
    }
}