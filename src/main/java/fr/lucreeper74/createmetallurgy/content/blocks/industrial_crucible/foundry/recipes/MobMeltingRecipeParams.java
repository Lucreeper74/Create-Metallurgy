package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.DamagedEntityIngredient;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.FoundryRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class MobMeltingRecipeParams extends FoundryRecipeParams {

    public static MapCodec<MobMeltingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            foundryCodec(MobMeltingRecipeParams::new).forGetter(Function.identity()),
            DamagedEntityIngredient.CODEC.fieldOf("entity").forGetter(MobMeltingRecipeParams::getEntityIngredient)
    ).apply(instance, (params, entityIngredient) -> {
        params.entityIngredient = entityIngredient;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, MobMeltingRecipeParams> STREAM_CODEC = streamCodec(MobMeltingRecipeParams::new);

    protected DamagedEntityIngredient entityIngredient;

    public DamagedEntityIngredient getEntityIngredient() {
        return entityIngredient;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        DamagedEntityIngredient.STREAM_CODEC.encode(buffer, entityIngredient);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        entityIngredient = DamagedEntityIngredient.STREAM_CODEC.decode(buffer);
    }
}
