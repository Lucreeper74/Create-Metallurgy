package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.GrindingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingRecipeSerializer;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.MeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.AlloyingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.MobMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.base.StandardFoundryRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum CMRecipeTypes implements IRecipeTypeInfo, StringRepresentable {

    MELTING(MeltingRecipe::new),
    ALLOYING(AlloyingRecipe::new),
    GRINDING(GrindingRecipe::new),
    BULK_MELTING(BulkMeltingRecipe::new),
    ENTITY_MELTING(MobMeltingRecipe::new),

    CASTING_IN_BASIN(CastingRecipeSerializer.CastingBasinRecipeSerializer::new),
    CASTING_IN_TABLE(CastingRecipeSerializer.CastingTableRecipeSerializer::new);

    private final ResourceLocation id;
    public final Supplier<RecipeSerializer<?>> serializerSupplier;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;

    private final Supplier<RecipeType<?>> type;

    CMRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = CMLang.asId(name());
        id = CreateMetallurgy.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        @Nullable DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject = Registers.TYPE_REGISTER.register(name, () ->
                RecipeType.simple(id));
        type = typeObject;
    }

    CMRecipeTypes(StandardProcessingRecipe.Factory<?> processingFactory) {
        this(() -> new StandardProcessingRecipe.Serializer<>(processingFactory));
    }

    CMRecipeTypes(StandardFoundryRecipe.Factory<?> foundryFactory) {
        this(() -> new StandardFoundryRecipe.Serializer<>(foundryFactory));
    }

    CMRecipeTypes(MobMeltingRecipe.Factory<?> mobFactory) {
        this(() -> new MobMeltingRecipe.Serializer<>(mobFactory));
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    @Override
    public @NotNull String getSerializedName() {
        return id.toString();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateMetallurgy.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CreateMetallurgy.MOD_ID);
    }
}