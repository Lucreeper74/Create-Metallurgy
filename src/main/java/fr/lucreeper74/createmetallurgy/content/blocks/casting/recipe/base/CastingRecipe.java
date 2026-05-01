package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class CastingRecipe implements Recipe<RecipeWrapper> {
    private IRecipeTypeInfo typeInfo;
    private RecipeSerializer<?> serializer;

    protected SizedFluidIngredient fluidIngredient;
    protected Ingredient ingredient;
    protected int processingDuration;
    protected boolean moldConsumed;
    protected CastingOutput result;

    public CastingRecipe(IRecipeTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
        this.serializer = typeInfo.getSerializer();
        this.ingredient = Ingredient.EMPTY;
        this.fluidIngredient = null;
        this.processingDuration = 0;
        this.moldConsumed = false;
        this.result = CastingOutput.EMPTY;
    }

    protected List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (ingredient.isEmpty() && moldConsumed) {
            errors.add("Recipe specified a mold condition. Mold condition have no impact on this recipe cause there is no mold.");
        }

        return errors;
    }

    public static boolean match(CastingBlockEntity be, Recipe<?> recipe, FluidStack testedFluid, boolean ignoreFluidAmount) {
        if (recipe instanceof CastingRecipe castingRecipe) {

            SizedFluidIngredient fluidIngredient = castingRecipe.getFluidIngredient();
            boolean fluidMatches = fluidIngredient.test(testedFluid);
            if (!ignoreFluidAmount)
                fluidMatches &= testedFluid.getAmount() >= fluidIngredient.amount();

            ItemStack mold = be.moldInv.getStackInSlot(0);
            Ingredient ingredient = castingRecipe.getIngredient();
            boolean ingredientMatches = ingredient.test(mold);

            return fluidMatches && ingredientMatches;
        }
        return false;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public SizedFluidIngredient getFluidIngredient() {
        return fluidIngredient;
    }

    protected final List<Either<SizedFluidIngredient, Ingredient>> ingredients() {
        List<Either<SizedFluidIngredient, Ingredient>> ingredients = new ArrayList<>();
        if (!fluidIngredient.ingredient().isEmpty())
            ingredients.add(Either.left(fluidIngredient));
        if (!ingredient.isEmpty())
            ingredients.add(Either.right(ingredient));
        return ingredients;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public boolean isMoldConsumed() {
        return moldConsumed;
    }

    @Override
    public boolean matches(RecipeWrapper input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.getStack();
    }

    public CastingOutput getResult() {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return typeInfo.getType();
    }

    public IRecipeTypeInfo getTypeInfo() {
        return typeInfo;
    }
}