package fr.lucreeper74.createmetallurgy.content.casting.recipe;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.casting.CastingBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;

public abstract class CastingRecipe implements Recipe<SmartInventory> {

    protected final ResourceLocation id;
    protected FluidIngredient fluidIngredient;
    protected Ingredient ingredient;
    protected int processingDuration;
    protected boolean moldConsumed;
    protected CastingOutput result;

    public CastingRecipe(ResourceLocation id) {
        this.id = id;
        this.ingredient = Ingredient.EMPTY;
        this.fluidIngredient = FluidIngredient.EMPTY;
        this.processingDuration = 0;
        this.moldConsumed = false;
        this.result = CastingOutput.EMPTY;

        validate(id);
    }

    public static boolean match(CastingBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof CastingRecipe castingRecipe) {
            FluidStack fluidInBuffer = be.getFluidBuffer();
            ItemStack mold = be.moldInv.getStackInSlot(0);
            Ingredient ingredient = castingRecipe.getIngredient();

            boolean fluidMatches = castingRecipe.getFluidIngredient().test(fluidInBuffer);
            boolean hasMold = !ingredient.isEmpty();
            boolean ingredientMatches = hasMold && ingredient.test(mold);

            return fluidMatches && (!hasMold || ingredientMatches);
        }
        return false;
    }

    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom recipe (" + recipeTypeId + ")";
        Logger logger = CreateMetallurgy.LOGGER;

        if(ingredient.isEmpty() && moldConsumed) {
            logger.warn(messageHeader + " specified a mold condition. Mold conditions have no impact on this recipe cause there is no mold.");
        }
    }


    @Override
    public boolean matches(SmartInventory inv, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SmartInventory pContainer) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result.getStack();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public RecipeType<?> getType() {
        return getTypeInfo().getType();
    }

    public abstract IRecipeTypeInfo getTypeInfo();

    public Ingredient getIngredient() {
        return ingredient;
    }

    public FluidIngredient getFluidIngredient() {
        return fluidIngredient;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public boolean isMoldConsumed() {
        return moldConsumed;
    }
}


