package fr.lucreeper74.createmetallurgy;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import fr.lucreeper74.createmetallurgy.kubejs.recipes.*;
import net.minecraft.resources.ResourceLocation;

public class KubeJSMetallurgyRecipeJSPlugin extends KubeJSPlugin {
    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(new ResourceLocation("createmetallurgy:casting_in_table"), CastingTableKubeJSRecipe.SCHEMA);
        event.register(new ResourceLocation("createmetallurgy:casting_in_basin"), CastingBasinKubeJSRecipe.SCHEMA);
        event.register(new ResourceLocation("createmetallurgy:grinding"), GrindingKubeJSRecipe.SCHEMA);
        event.register(new ResourceLocation("createmetallurgy:alloying"), AlloyingKubeJSRecipe.SCHEMA);
        event.register(new ResourceLocation("createmetallurgy:melting"), MeltingKubeJSRecipe.SCHEMA);
    }
}