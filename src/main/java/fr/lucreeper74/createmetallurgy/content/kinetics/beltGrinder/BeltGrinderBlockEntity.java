package fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BeltGrinderBlockEntity extends KineticBlockEntity {

    private static final Object grindingRecipesKey = new Object();
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    public ProcessingInventory inv;
    public int processingTick;
    private int recipeIndex;
    private FilteringBehaviour filtering;

    public BeltGrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new ProcessingInventory(this::start);
        itemCapability = LazyOptional.of(() -> inv);
        recipeIndex = 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        filtering = new FilteringBehaviour(this, new BeltGrinderFilterSlot()).forRecipes();
        behaviours.add(filtering);
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("inv", inv.serializeNBT());
        compound.putInt("processTicks", processingTick);
        compound.putInt("RecipeIndex", recipeIndex);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inv.deserializeNBT(compound.getCompound("inv"));
        processingTick = compound.getInt("processTicks");
        recipeIndex = compound.getInt("RecipeIndex");
        super.read(compound, clientPacket);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inv);
    }

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0)
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
                if (remainder.equals(stack, false))
                    continue;
                inv.setStackInSlot(slot, remainder);
                changed = true;
            }
            if (changed) {
                setChanged();
                sendData();
            }
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

        List<? extends Recipe<?>> recipes = getRecipes();
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

        Recipe<?> recipe = recipes.get(recipeIndex);
        if (recipe instanceof CuttingRecipe) {
            time = ((CuttingRecipe) recipe).getProcessingDuration();
        }

        inv.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
        inv.recipeDuration = inv.remainingTime;
        inv.appliedRecipe = false;
        sendData();
    }

    private List<? extends Recipe<?>> getRecipes() {
        Optional<GrindingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inv.getStackInSlot(0),
                CMRecipeTypes.GRINDING.getType(), GrindingRecipe.class);
        if (assemblyRecipe.isPresent() && filtering.test(assemblyRecipe.get()
                .getResultItem(level.registryAccess())))
            return ImmutableList.of(assemblyRecipe.get());

        Predicate<Recipe<?>> types = RecipeConditions.isOfType(CMRecipeTypes.GRINDING.getType(), com.simibubi.create.AllRecipeTypes.SANDPAPER_POLISHING.getType());

        List<Recipe<?>> startedSearch = RecipeFinder.get(grindingRecipesKey, level, types);
        return startedSearch.stream()
                .filter(RecipeConditions.outputMatchesFilter(filtering))
                .filter(RecipeConditions.firstIngredientMatches(inv.getStackInSlot(0)))
                .collect(Collectors.toList());
    }

    private void applyRecipe() {
        List<? extends Recipe<?>> recipes = getRecipes();
        if (recipes.isEmpty())
            return;
        if (recipeIndex >= recipes.size())
            recipeIndex = 0;

        Recipe<?> recipe = recipes.get(recipeIndex);

        int rolls = inv.getStackInSlot(0)
                .getCount();
        inv.clear();

        List<ItemStack> list = new ArrayList<>();
        for (int roll = 0; roll < rolls; roll++) {
            List<ItemStack> results = new LinkedList<ItemStack>();
            if (recipe instanceof GrindingRecipe)
                results = ((GrindingRecipe) recipe).rollResults();
            else if (recipe instanceof SandPaperPolishingRecipe)
                results.add(recipe.getResultItem(level.registryAccess())
                        .copy());

            for (int i = 0; i < results.size(); i++) {
                ItemStack stack = results.get(i);
                ItemHelper.addToList(stack, list);
            }
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

    //Client Things
    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        float speed = Math.abs(getSpeed());
        if (speed == 0)
            return;
        if(!inv.isEmpty() && AnimationTickHolder.getTicks() % 4 == 0) {
            float pitch = Mth.clamp((speed / 256f) * 2f, .5f, 1.6f);
            AllSoundEvents.SANDING_SHORT.playAt(level, worldPosition, .3f, level.random.nextFloat() * 0.5F + pitch, true);
        }
    }

    protected void spawnParticles(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return;

        //TODO if metals items recipes, then display spark particles

        ParticleOptions particleData;
        float speed = 1;
        if (stack.getItem() instanceof BlockItem)
            particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
                    .defaultBlockState());
        else {
            particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);
            speed = .125f;
        }

        RandomSource r = level.random;
        Vec3 vec = getItemMovementVec();
        Vec3 pos = VecHelper.getCenterOf(this.worldPosition);
        float offset = inv.recipeDuration != 0 ? inv.remainingTime / inv.recipeDuration : 0;
        offset /= 2;
        if (inv.appliedRecipe)
            offset -= .5f;
        level.addParticle(particleData, pos.x() + -vec.x * offset, pos.y() + .45f, pos.z() + -vec.z * offset,
                -vec.x * speed, r.nextFloat() * speed, -vec.z * speed);
    }
}
