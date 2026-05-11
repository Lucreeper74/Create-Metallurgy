package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMShapes;
import fr.lucreeper74.createmetallurgy.utils.CMConnectivityHandler;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import fr.lucreeper74.createmetallurgy.utils.SideAttachment;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class CrucibleBlock extends Block implements IWrenchable, IBE<CrucibleBlockEntity> {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final BooleanProperty WINDOW = BooleanProperty.create("window");

    public CrucibleBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(super.defaultBlockState()
                .setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, Shape.PLAIN)
                .setValue(WINDOW, false));
    }

    public static boolean isCrucible(BlockState state) {
        return state.getBlock() instanceof CrucibleBlock;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, CrucibleBlockEntity::updateConnectivity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, SHAPE, WINDOW);
    }

    @Override
    public boolean canBeReplaced(BlockState pState, Fluid pFluid) {
        return false;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        CrucibleBlockEntity ladle = CMConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (ladle == null)
            return 0;
        if (ladle.getControllerBE() == null)
            return 0;
        return ladle.luminosity;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (state.getValue(SHAPE) == Shape.INNER)
            return InteractionResult.PASS;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        level.setBlockAndUpdate(pos, state.cycle(WINDOW));
        level.playSound(null, pos, SoundEvents.DEEPSLATE_PLACE, SoundSource.PLAYERS, 1f,
                .2f + RandomSource.create().nextFloat());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucibleBE) {
            Direction clickedSide = context.getClickedFace();

            SideAttachment removedAttachment = crucibleBE.getSideAttachment(clickedSide);

            if (crucibleBE.setAttachment(clickedSide, SideAttachment.NONE, false)) {
                removedAttachment.onRemove(context);

                ItemStack removedItem = removedAttachment.getItem();
                if (!removedItem.isEmpty()) {
                    if (!context.getPlayer().isCreative())
                        context.getPlayer().getInventory().placeItemBackInInventory(removedItem);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this)
            withBlockEntityDo(pLevel, pCurrentPos, CrucibleBlockEntity::updateFoundryTemperature);
        return pState;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean onClient = level.isClientSide;

        if (heldItem.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.isCreative())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        FluidHelper.FluidExchange exchange = null;
        CrucibleBlockEntity be = CMConnectivityHandler.partAt(getBlockEntityType(), level, pos);
        if (be == null)
            return ItemInteractionResult.FAIL;

        IFluidHandler tankCapability = level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (tankCapability == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        FluidStack prevFluidInTank = tankCapability.getFluidInTank(0)
                .copy();

        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, heldItem, be))
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(level, player, hand, heldItem, be))
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack))
                return ItemInteractionResult.SUCCESS;
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInTank = tankCapability.getFluidInTank(0);

        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            Fluid fluid = fluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInTank);
        }

        if (exchange == FluidHelper.FluidExchange.TANK_TO_ITEM) {
            Fluid fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInTank);
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth.clamp(1 - (1f * fluidInTank.getAmount() / (CrucibleBlockEntity.getCapacityFactor() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.getRandom().nextFloat() - .5f) / 4f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!FluidStack.isSameFluidSameComponents(fluidInTank, prevFluidInTank)) {
            if (be instanceof CrucibleBlockEntity) {
                CrucibleBlockEntity controllerBE = be.getControllerBE();
                if (controllerBE != null) {
                    if (onClient) {
                        BlockParticleOption blockParticleData =
                                new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                        float fluidLevel = (float) fluidInTank.getAmount() / tankCapability.getTankCapacity(0);

                        boolean reversed = fluidInTank.getFluid()
                                .getFluidType()
                                .isLighterThanAir();
                        if (reversed)
                            fluidLevel = 1 - fluidLevel;

                        Vec3 vec = hitResult.getLocation();
                        vec = new Vec3(vec.x, controllerBE.getBlockPos()
                                .getY() + fluidLevel * (controllerBE.getHeight() - .5f) + .25f, vec.z);
                        Vec3 motion = player.position()
                                .subtract(vec)
                                .scale(1 / 20f);
                        vec = vec.add(motion);
                        level.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                        return ItemInteractionResult.SUCCESS;
                    }
                    controllerBE.sendData();
                    controllerBE.setChanged();
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Boolean bottom = state.getValue(BOTTOM);
        Shape shape = state.getValue(SHAPE);
        Direction direction = shape.toDirection();

        if (bottom)
            return switch (shape) {
                case NE, SE, NW, SW -> CMShapes.CRUCIBLE_CORNER_BOTTOM.get(direction);
                case NORTH, SOUTH, EAST, WEST -> CMShapes.CRUCIBLE_WALL_BOTTOM.get(direction);
                case PLAIN -> CMShapes.CRUCIBLE_SINGLE_BOTTOM;
                default -> CMShapes.CRUCIBLE_BOTTOM;
            };
        else
            return switch (shape) {
                case NE, SE, NW, SW -> CMShapes.CRUCIBLE_CORNER.get(direction);
                case NORTH, SOUTH, EAST, WEST -> CMShapes.CRUCIBLE_WALL.get(direction);
                case PLAIN -> CMShapes.CRUCIBLE_SINGLE;
                default -> Shapes.empty();
            };
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrucibleBlockEntity crucibleBE) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), crucibleBE.foundrySlot.removeStack());

                // Drop Attachments
                for (Direction side : Iterate.directions) {
                    SideAttachment mode = crucibleBE.getSideAttachment(side);
                    if (mode.equals(SideAttachment.NONE))
                        continue;
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), mode.getItem());
                }

                world.removeBlockEntity(pos);
                CMConnectivityHandler.splitMulti(crucibleBE);
            }
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);

        if (!CMBlocks.INDUSTRIAL_CRUCIBLE.has(worldIn.getBlockState(entityIn.blockPosition())))
            return;

        if (entityIn instanceof ItemEntity itemEntity) {
            withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {
                CrucibleBlockEntity controller = be.getControllerBE();
                if (controller != null) {
                    ItemStack insertItem = ItemHandlerHelper.insertItem(controller.foundryData.getInputInv(), itemEntity.getItem()
                            .copy(), false);

                    if (insertItem.isEmpty()) {
                        itemEntity.discard();
                        return;
                    }
                    itemEntity.setItem(insertItem);
                }
            });
        } else {
            withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {
                if (be != null) {
                    CrucibleBlockEntity controller = be.getControllerBE();
                    if (controller != null)
                        controller.processFallOnEntity(entityIn);
                }
            });
        }
    }

    @Override
    public Class<CrucibleBlockEntity> getBlockEntityClass() {
        return CrucibleBlockEntity.class;
    }

    public BlockEntityType<? extends CrucibleBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.INDUSTRIAL_CRUCIBLE.get();
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        boolean x = mirror == Mirror.FRONT_BACK;
        return switch (state.getValue(SHAPE)) {
            case NE -> state.setValue(SHAPE, x ? Shape.NW : Shape.SE);
            case NW -> state.setValue(SHAPE, x ? Shape.NE : Shape.SW);
            case SE -> state.setValue(SHAPE, x ? Shape.SW : Shape.NE);
            case SW -> state.setValue(SHAPE, x ? Shape.SE : Shape.NW);
            default -> state;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        return switch (state.getValue(SHAPE)) {
            case NE -> state.setValue(SHAPE, Shape.SE);
            case NW -> state.setValue(SHAPE, Shape.NE);
            case SE -> state.setValue(SHAPE, Shape.SW);
            case SW -> state.setValue(SHAPE, Shape.NW);
            default -> state;
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        withBlockEntityDo(level, pos, be -> {
            CrucibleBlockEntity controller = be.getControllerBE();
            if (controller != null && state.getValue(BOTTOM)) {
                if (!controller.getTank().isEmpty() && random.nextInt(200) == 0)
                    level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.7F + random.nextFloat() * 0.15F, false);

                if (controller.foundryData.getCurrentHeat() > 0 && random.nextInt(3) == 0) {
                    float radius = controller.getWidth() / 2f;
                    Vec3 c = Vec3.atLowerCornerOf(controller.getBlockPos()).add(radius, controller.getHeight() * controller.getTank().getFillState(), radius);
                    Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, random, radius - 4 / 16f)
                            .multiply(1, 0, 1));

                    level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y + 3 / 16f, v.z, 0, 0, 0);
                }
            }
        });
    }

    // Tanks are less noisy when placed in batch
    public static final SoundType SILENCED_BRICKS =
            new DeferredSoundType(0.1F, 1.5F, () -> SoundEvents.DEEPSLATE_BRICKS_BREAK, () -> SoundEvents.DEEPSLATE_BRICKS_STEP,
                    () -> SoundEvents.DEEPSLATE_BRICKS_PLACE, () -> SoundEvents.DEEPSLATE_BRICKS_HIT, () -> SoundEvents.DEEPSLATE_BRICKS_FALL);

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData()
                .contains("SilenceTankSound"))
            return SILENCED_BRICKS;
        return soundType;
    }

    public enum Shape implements StringRepresentable {
        PLAIN, // For Single blocks
        INNER, // For Inners
        NW, SW, NE, SE, // For Corners
        NORTH, SOUTH, WEST, EAST; // For Walls

        @Override
        public String getSerializedName() {
            return CMLang.asId(name());
        }

        public boolean isWall() {
            return this.equals(NORTH) || this.equals(SOUTH) || this.equals(WEST) || this.equals(EAST);
        }

        public boolean isCorner() {
            return this.equals(NW) || this.equals(SW) || this.equals(NE) || this.equals(SE);
        }

        public Direction toDirection() {
            return switch (this) {
                case NE, NORTH -> Direction.NORTH;
                case NW, WEST -> Direction.WEST;
                case SW, SOUTH -> Direction.SOUTH;
                case SE, EAST -> Direction.EAST;
                default -> Direction.DOWN;
            };
        }
    }
}