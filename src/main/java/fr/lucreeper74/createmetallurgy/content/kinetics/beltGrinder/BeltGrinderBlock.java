package fr.lucreeper74.createmetallurgy.content.kinetics.beltGrinder;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltGrinderBlock extends HorizontalKineticBlock implements IBE<BeltGrinderBlockEntity> {
    public static DamageSource damageSourceGrinder = new DamageSource(CreateMetallurgy.MOD_ID + ".mechanical_grinder");

    public BeltGrinderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_14PX.get(Direction.UP);
    }

    @Override
    public Class<BeltGrinderBlockEntity> getBlockEntityClass() {
        return BeltGrinderBlockEntity.class;
    }

    public BlockEntityType<? extends BeltGrinderBlockEntity> getBlockEntityType() {
        return CMBlockEntityTypes.BELT_GRINDER.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult ray) {
        if (player.isSpectator() || !player.getItemInHand(handIn).isEmpty())
            return InteractionResult.PASS;
        if (ray.getDirection() != Direction.UP)
            return InteractionResult.PASS;

        return onBlockEntityUse(worldIn, pos, be -> {
            for (int i = 0; i < be.inv.getSlots(); i++) {
                ItemStack heldItemStack = be.inv.getStackInSlot(i);
                if (!worldIn.isClientSide && !heldItemStack.isEmpty())
                    player.getInventory()
                            .placeItemBackInInventory(heldItemStack);
            }
            be.inv.clear();
            be.notifyUpdate();
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof ItemEntity)
            return;
        if (entityIn instanceof Player player)
            if (player.isCreative())
                return;
        if (!new AABB(pos).deflate(.1f)
                .intersects(entityIn.getBoundingBox()))
            return;

        withBlockEntityDo(worldIn, pos, be -> {
            float speed = Mth.abs(be.getSpeed());
            if (be.getSpeed() == 0)
                return;

            for (ItemStack armor : entityIn.getArmorSlots()) {
                if (armor.isEmpty() || !armor.isDamageableItem() || armor.getDamageValue() >= armor.getMaxDamage())
                    entityIn.hurt(damageSourceGrinder, (float) DrillBlock.getDamage(speed));

                //Hurt armor every 10 ticks at max speed to every 90 ticks at lower speed -> f(x)= (-10/32) * x + 90
                if(AnimationTickHolder.getTicks() % Math.round((-10f * speed) / 32f + 90) == 0)
                    armor.hurt(1, entityIn.level.getRandom(), null);

                if(!armor.isEmpty()) {
                    float pitch = (speed / 256f) + .8f;
                    entityIn.playSound(SoundEvents.GRINDSTONE_USE, .3f, entityIn.level.random.nextFloat() * 0.2F + pitch);
                    Level level = entityIn.level;
                    RandomSource r = level.getRandom();
                    Vec3 c = VecHelper.getCenterOf(be.getBlockPos());
                    Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .25f)
                            .multiply(1, 0, 1));
                    level.addParticle(ParticleTypes.CRIT, v.x, v.y + .4f, v.z, 0, 0, 0);
                }
                return;
                }
        });
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!(entityIn instanceof ItemEntity))
            return;
        if (entityIn.level.isClientSide)
            return;

        BlockPos pos = entityIn.blockPosition();
        withBlockEntityDo(entityIn.level, pos, be -> {
            if (be.getSpeed() == 0)
                return;
            be.insertItem((ItemEntity) entityIn);
        });
    }
}
