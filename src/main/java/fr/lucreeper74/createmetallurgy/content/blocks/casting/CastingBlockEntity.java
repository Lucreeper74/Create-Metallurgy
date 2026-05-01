package fr.lucreeper74.createmetallurgy.content.blocks.casting;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingRecipe;
import fr.lucreeper74.createmetallurgy.data.recipes.CMMetals;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CastingBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    protected ScrollOptionBehaviour<LockMode> lockSelect;

    public CastingFluidTank inputTank;
    public SmartInventory inv;
    public SmartInventory moldInv;
    public IItemHandlerModifiable itemCapability;

    private boolean contentsChanged;

    protected CastingRecipe currentRecipe;
    public int processingTick;
    public boolean running;

    // For rendering purposes :
    public int totalRecipeTime;
    public ItemStack currentRecipeOutput;

    public CastingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inputTank = new CastingFluidTank(this, () -> contentsChanged = true);

        inv = new SmartInventory(1, this, 1, true).forbidInsertion();
        moldInv = new SmartInventory(1, this, 1, true);
        itemCapability = new CombinedInvWrapper(inv, moldInv);

        contentsChanged = true;

        currentRecipeOutput = ItemStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));

        lockSelect = new ScrollOptionBehaviour<>(LockMode.class,
                CMLang.translateDirect("casting.lock_mode"), this, new CastingBlockLockSlot());
        behaviours.add(lockSelect);

        lockSelect.withCallback(setting -> updateMoldInvLock());
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("inputTank", inputTank.writeToNBT(registries));

        compound.put("inv", inv.serializeNBT(registries));
        compound.put("moldInv", moldInv.serializeNBT(registries));

        compound.putInt("processingTick", processingTick);
        compound.putBoolean("running", running);

        compound.put("currentRecipeOutput", currentRecipeOutput.saveOptional(registries));
        compound.putInt("totalRecipeTime", totalRecipeTime);

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        inputTank.readFromNBT(registries, compound.getCompound("inputTank"), clientPacket);

        inv.deserializeNBT(registries, compound.getCompound("inv"));
        moldInv.deserializeNBT(registries, compound.getCompound("moldInv"));

        processingTick = compound.getInt("processingTick");
        running = compound.getBoolean("running");

        currentRecipeOutput = ItemStack.parseOptional(registries, compound.getCompound("currentRecipeOutput"));
        totalRecipeTime = compound.getInt("totalRecipeTime");

        LockMode oldLockState = lockSelect.get();
        super.read(compound, registries, clientPacket);
        if (oldLockState != lockSelect.get() && !clientPacket)
            updateMoldInvLock();
    }

    public void readOnlyItems(CompoundTag compound, HolderLookup.Provider registries) {
        inv.deserializeNBT(registries, compound.getCompound("inv"));
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(getLevel(), worldPosition, inv);
        ItemHelper.dropContents(getLevel(), worldPosition, moldInv);
    }

    @Override
    public void tick() {
        super.tick();

        if (getLevel() == null)
            return;

        inputTank.tick();

        if (!getLevel().isClientSide) {
            if (contentsChanged) {
                contentsChanged = false;
                updateCasting();
            }
        }

        if (running) {
            if (getLevel().isClientSide)
                spawnParticles();

            // Continue to process even if the recipe can no longer be made, will result in a failed one.

            if (processingTick <= 0) {
                // Recipe finished, apply the recipe
                if (!getLevel().isClientSide) {
                    applyRecipe();
                    playProcessSound();
                }


            } else {
                // Currently casting, tick counter handling
                if (isInAirCurrent(getLevel(), getBlockPos(), this))
                    processingTick -= 2;
                else
                    --processingTick;
            }
        }
    }

    public void startProcess() {
        moldInv.forbidInsertion();
        processingTick = currentRecipe.getProcessingDuration();
        running = true;
    }

    public void updateCasting() {
        if (currentRecipe != null)
            return; // If already a recipe, no need to fetch one

        if (inputTank.isEmpty())
            return; // No fluid contained

        // Get matching recipes for the current content
        List<RecipeHolder<? extends Recipe<?>>> recipes = getMatchingRecipes(getFluidTank().getFluid(), false);
        if (recipes.isEmpty())
            return; // No recipe found

        currentRecipe = (CastingRecipe) recipes.getFirst().value();
        currentRecipeOutput = currentRecipe.getResultItem(getLevel().registryAccess()).copy();
        totalRecipeTime = currentRecipe.getProcessingDuration();

        if (running)
            return; // If already running before, keep it

        startProcess();
        sendData();
    }

    public void applyRecipe() {
        FluidStack fluidInTank = getFluidTank().getFluidInTank(0);
        if (matchCastingRecipe(currentRecipe, getFluidTank().getFluid(), false)) {
            inv.setStackInSlot(0, currentRecipeOutput);
            fluidInTank.shrink(currentRecipe.getFluidIngredient().amount());

            if (currentRecipe.isMoldConsumed())
                moldInv.setStackInSlot(0, ItemStack.EMPTY);
        } else {
            int ingot_Amount = CMMetals.ItemType.INGOT.getFluidAmount();
            if (CMFluids.isMoltenMaterial(fluidInTank.getFluid()) && fluidInTank.getAmount() >= ingot_Amount) {
                fluidInTank.shrink(ingot_Amount);
                inv.setStackInSlot(0, CMItems.SLAG.asStack());
            }
        }

        reset();
    }

    public ItemStack getCurrentRecipeOutput() {
        return currentRecipeOutput;
    }

    protected void spawnParticles() {
        RandomSource r = getLevel().getRandom();
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                .multiply(1, 0, 1));
        if (r.nextInt(8) == 0)
            getLevel().addParticle(ParticleTypes.SMOKE, v.x, v.y + .45, v.z, 0, 0, 0);
    }

    public FluidTank getFluidTank() {
        return inputTank;
    }

    public static boolean isInAirCurrent(Level level, BlockPos pos, BlockEntity be) {
        int range = 3;

        for (Direction direction : Direction.values()) {
            for (int i = 0; i <= range; i++) {
                BlockPos nearbyPos = pos.relative(direction, i);
                BlockState nearbyState = level.getBlockState(nearbyPos);

                if (nearbyState.getBlock() instanceof EncasedFanBlock) {
                    EncasedFanBlockEntity fanBe = (EncasedFanBlockEntity) level.getBlockEntity(nearbyPos);
                    Direction facing = nearbyState.getValue(EncasedFanBlock.FACING);
                    BlockEntity facingBe = level.getBlockEntity(nearbyPos.relative(facing, i));
                    float flowDist = fanBe.airCurrent.maxDistance;

                    if (be == facingBe && flowDist != 0 && flowDist >= i - 1) return true;
                }
            }
        }
        return false;
    }

    protected <I extends RecipeInput> boolean matchCastingRecipe(Recipe<I> recipe, FluidStack testedFluid, boolean ignoreFluidAmount) {
        if (recipe == null || !inv.getStackInSlot(0).isEmpty())
            return false;
        return CastingRecipe.match(this, recipe, testedFluid, ignoreFluidAmount);
    }

    public List<RecipeHolder<? extends Recipe<?>>> getMatchingRecipes(FluidStack testedFluid, boolean ignoreFluidAmount) {
        List<RecipeHolder<? extends Recipe<?>>> list = RecipeFinder.get(getRecipeCacheKey(), getLevel(), this::matchStaticFilters);
        return list.stream()
                .filter(recipe -> matchCastingRecipe(recipe.value(), testedFluid, ignoreFluidAmount))
                .sorted(Comparator.comparingInt(r -> r.value().getIngredients()
                        .size()))
                .collect(Collectors.toList());
    }

    public int getRequirementFromFluid(FluidStack fluid) {
        if (currentRecipe != null || running)
            return 0;

        // Check for recipes for a fluid requested in Casting Fluid Tank
        List<RecipeHolder<? extends Recipe<?>>> recipes = getMatchingRecipes(fluid, true);
        if (recipes.isEmpty())
            return 0;

        return ((CastingRecipe) recipes.getFirst().value()).getFluidIngredient().amount();
    }

    public void reset() {
        moldInv.allowInsertion();
        inputTank.reset();
        processingTick = -1;
        currentRecipe = null;
        running = false;
        currentRecipeOutput = ItemStack.EMPTY;
        sendData();
    }

    public void updateMoldInvLock() {
        switch (lockSelect.get()) {
            case LOCKED -> moldInv.forbidExtraction();
            case UNLOCKED -> moldInv.allowExtraction();
            default -> {
            }
        }
    }

    protected abstract void playProcessSound();

    protected abstract boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe);

    protected abstract Object getRecipeCacheKey();

    // Client Stuff
    public abstract LangBuilder getGoggleTooltip();

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        getGoggleTooltip().forGoggles(tooltip);

        IFluidHandler fluids = getFluidTank();
        boolean isEmpty = true;

        for (int i = 0; i < itemCapability.getSlots(); i++) {
            ItemStack stackInSlot = itemCapability.getStackInSlot(i);
            if (stackInSlot.isEmpty())
                continue;
            CMLang.text("")
                    .add(Component.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CMLang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        FluidStack fluidStack = fluids.getFluidInTank(0);
        if (!fluidStack.isEmpty()) {
            CMLang.text("")
                    .add(CMLang.fluidName(fluidStack)
                            .add(CMLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CMLang.number(fluidStack.getAmount())
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.removeFirst();

        return true;
    }

    public enum LockMode implements INamedIconOptions {
        UNLOCKED(AllIcons.I_CONFIG_UNLOCKED),
        LOCKED(AllIcons.I_CONFIG_LOCKED);

        private final String translationKey;
        private final AllIcons icon;

        LockMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "createmetallurgy.casting.lock_mode." + CMLang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }
}