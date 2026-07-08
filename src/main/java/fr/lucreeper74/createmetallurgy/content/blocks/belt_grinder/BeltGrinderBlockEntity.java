package fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BeltGrinderBlockEntity extends KineticBlockEntity implements Clearable {

    private static final Object grindingRecipesKey = new Object();
    public ProcessingInventory inv;
    public int processingTick;
    private int recipeIndex;
    private FilteringBehaviour filtering;

    public BeltGrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new ProcessingInventory(this::start);
        recipeIndex = 0;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.BELT_GRINDER.get(),
                (be, context) -> be.inv
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        filtering = new FilteringBehaviour(this, new BeltGrinderFilterSlot()).forRecipes();
        behaviours.add(filtering);
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("inv", inv.serializeNBT(registries));
        compound.putInt("processTicks", processingTick);
        compound.putInt("RecipeIndex", recipeIndex);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        inv.deserializeNBT(registries, compound.getCompound("inv"));
        processingTick = compound.getInt("processTicks");
        recipeIndex = compound.getInt("RecipeIndex");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inv);
    }

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0 || !isSpeedRequirementFulfilled())
            return;

        if (inv.remainingTime == -1) {
            if (!inv.isEmpty() && !inv.appliedRecipe)
                start(inv.getStackInSlot(0));
            return;
        }

        float processingSpeed = Mth.clamp(Math.abs(getSpeed()) / 24, 1, 128);
        inv.remainingTime -= processingSpeed;

        if (inv.remainingTime > 0)
            spawnParticles(inv.getStackInSlot(0));

        if (inv.remainingTime < 5 && !inv.appliedRecipe) {
            if (level.isClientSide && !isVirtual())
                return;
            applyRecipe();
            inv.appliedRecipe = true;
            inv.recipeDuration = 20;
            inv.remainingTime = 20;
            sendData();
        }

        Vec3 itemMovement = getItemMovementVec();
        Direction itemMovementFacing = Direction.getNearest(itemMovement.x, itemMovement.y, itemMovement.z);
        if (inv.remainingTime > 0)
            return;
        inv.remainingTime = 0;

        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            ItemStack tryExportingToBeltFunnel = getBehaviour(DirectBeltInputBehaviour.TYPE)
                    .tryExportingToBeltFunnel(stack, itemMovementFacing.getOpposite(), false);
            if (tryExportingToBeltFunnel != null) {
                if (tryExportingToBeltFunnel.getCount() != stack.getCount()) {
                    inv.setStackInSlot(slot, tryExportingToBeltFunnel);
                    notifyUpdate();
                    return;
                }
                if (!tryExportingToBeltFunnel.isEmpty())
                    return;
            }
        }

        BlockPos nextPos = worldPosition.offset(BlockPos.containing(itemMovement));
        DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(level, nextPos, DirectBeltInputBehaviour.TYPE);
        if (behaviour != null) {
            boolean changed = false;
            if (!behaviour.canInsertFromSide(itemMovementFacing))
                return;
            if (level.isClientSide && !isVirtual())
                return;
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack.isEmpty())
                    continue;
                ItemStack remainder = behaviour.handleInsertion(stack, itemMovementFacing, false);
                if (ItemStack.matches(remainder, stack))
                    continue;
                inv.setStackInSlot(slot, remainder);
                changed = true;
            }
            if (changed) {
                setChanged();
                sendData();
            }
            return;
        }

        // Eject Items
        Vec3 outPos = VecHelper.getCenterOf(worldPosition)
                .add(itemMovement.scale(.5f)
                        .add(0, .5, 0));
        Vec3 outMotion = itemMovement.scale(.0625)
                .add(0, .125, 0);
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            ItemEntity entityIn = new ItemEntity(level, outPos.x, outPos.y, outPos.z, stack);
            entityIn.setDeltaMovement(outMotion);
            level.addFreshEntity(entityIn);
        }
        inv.clear();
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        inv.remainingTime = -1;
        sendData();
    }

    public Vec3 getItemMovementVec() {
        boolean alongX = getBlockState().getValue(BeltGrinderBlock.HORIZONTAL_FACING).getAxis() != Direction.Axis.X;
        int offset = getSpeed() < 0 ? -1 : 1;
        return new Vec3(offset * (alongX ? 1 : 0), 0, offset * (alongX ? 0 : -1));
    }

    private void start(ItemStack inserted) {
        if (inv.isEmpty())
            return;
        if (level.isClientSide && !isVirtual())
            return;

        List<RecipeHolder<? extends Recipe<?>>> recipes = getRecipes();
        boolean valid = !recipes.isEmpty();
        int time = 50;

        if (recipes.isEmpty()) {
            inv.remainingTime = inv.recipeDuration = 10;
            inv.appliedRecipe = false;
            sendData();
            return;
        }

        if (valid) {
            recipeIndex++;
            if (recipeIndex >= recipes.size())
                recipeIndex = 0;
        }

        Recipe<?> recipe = recipes.get(recipeIndex).value();
        if (recipe instanceof GrindingRecipe) {
            time = ((GrindingRecipe) recipe).getProcessingDuration();
        }

        inv.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
        inv.recipeDuration = inv.remainingTime;
        inv.appliedRecipe = false;
        sendData();
    }

    private List<RecipeHolder<? extends Recipe<?>>> getRecipes() {
        Optional<RecipeHolder<GrindingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inv.getStackInSlot(0),
                CMRecipeTypes.GRINDING.getType(), GrindingRecipe.class);
        if (assemblyRecipe.isPresent() && filtering.test(assemblyRecipe.get().value()
                .getResultItem(level.registryAccess())))
            return ImmutableList.of(assemblyRecipe.get());

        Predicate<RecipeHolder<? extends Recipe<?>>> types = RecipeConditions.isOfType(CMRecipeTypes.GRINDING.getType(), AllRecipeTypes.SANDPAPER_POLISHING.getType());

        List<RecipeHolder<? extends Recipe<?>>> startedSearch = RecipeFinder.get(grindingRecipesKey, level, types);
        return startedSearch.stream()
                .filter(RecipeConditions.outputMatchesFilter(filtering))
                .filter(RecipeConditions.firstIngredientMatches(inv.getStackInSlot(0)))
                .collect(Collectors.toList());
    }

    private void applyRecipe() {
        List<RecipeHolder<? extends Recipe<?>>> recipes = getRecipes();
        if (recipes.isEmpty())
            return;
        if (recipeIndex >= recipes.size())
            recipeIndex = 0;

        Recipe<?> recipe = recipes.get(recipeIndex).value();

        int rolls = inv.getStackInSlot(0)
                .getCount();
        inv.clear();

        List<ItemStack> list = new ArrayList<>();
        for (int roll = 0; roll < rolls; roll++) {
            List<ItemStack> results = new LinkedList<ItemStack>();
            if (recipe instanceof GrindingRecipe)
                results = ((GrindingRecipe) recipe).rollResults(level.getRandom());
            else if (recipe instanceof SandPaperPolishingRecipe)
                results.add(recipe.getResultItem(level.registryAccess())
                        .copy());

            for (ItemStack stack : results)
                ItemHelper.addToList(stack, list);
        }
        for (int slot = 0; slot < list.size() && slot + 1 < inv.getSlots(); slot++)
            inv.setStackInSlot(slot + 1, list.get(slot));
    }

    public void insertItem(ItemEntity entity) {
        if (!inv.isEmpty())
            return;
        if (!entity.isAlive())
            return;
        if (level.isClientSide)
            return;

        inv.clear();
        ItemStack remainder = inv.insertItem(0, entity.getItem()
                .copy(), false);
        if (remainder.isEmpty())
            entity.discard();
        else
            entity.setItem(remainder);
    }

    // Client Things
    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        float speed = Math.abs(getSpeed());
        if (speed == 0)
            return;
        if (!inv.isEmpty() && AnimationTickHolder.getTicks() % 4 == 0) {
            float pitch = Mth.clamp((speed / 256f) * 2f, .5f, 1.6f);
            AllSoundEvents.SANDING_SHORT.playAt(level, worldPosition, .3f, level.getRandom().nextFloat() * 0.5F + pitch, true);
        }
    }

    protected void spawnParticles(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return;

        ParticleOptions particleData;
        float speed = 1;
        if (stack.getItem() instanceof BlockItem)
            particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
                    .defaultBlockState());
        else {
            particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);
            speed = .125f;
        }

        RandomSource r = level.getRandom();
        Vec3 vec = getItemMovementVec();
        Vec3 pos = VecHelper.getCenterOf(this.worldPosition);
        float offset = inv.recipeDuration != 0 ? inv.remainingTime / inv.recipeDuration : 0;
        offset /= 2;
        if (inv.appliedRecipe)
            offset -= .5f;
        level.addParticle(particleData, pos.x() + -vec.x * offset, pos.y() + .45f, pos.z() + -vec.z * offset,
                -vec.x * speed, r.nextFloat() * speed, -vec.z * speed);
    }

    @Override
    public void clearContent() {
        inv.clear();
        filtering.setFilter(ItemStack.EMPTY);
    }
}
