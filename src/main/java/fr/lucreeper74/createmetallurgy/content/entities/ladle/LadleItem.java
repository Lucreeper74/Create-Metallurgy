package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.config.CMConfig;
import fr.lucreeper74.createmetallurgy.registries.CMDataComponents;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import static fr.lucreeper74.createmetallurgy.content.fluids.MoltenFluidType.MOLTEN_FLUID_BURNING_TIME;

public class LadleItem extends PackageItem {

    public LadleItem(Properties properties, PackageStyle style) {
        super(properties, style);

        LadleStyles.ALL_LADLES.add(this);
        PackageStyles.ALL_BOXES.remove(this); // Avoid touching Create's packages list

        if (style.rare()) {
            LadleStyles.RARE_LADLES.add(this);
            PackageStyles.RARE_BOXES.remove(this);
        } else {
            LadleStyles.STANDARD_LADLES.add(this);
            PackageStyles.STANDARD_BOXES.remove(this);
        }
    }

    public static int getLadleCapacity() {
        return CMConfig.server().ladleCapacity.get();
    }

    public static int getMaxAddrCount() {
        return CMConfig.server().ladleMaxAddr.get();
    }

    public static boolean isLadle(ItemStack stack) {
        return stack.getItem() instanceof LadleItem;
    }

    public static void clearRemainAddrs(ItemStack ladle) {
        ladle.remove(CMDataComponents.LADLE_REMAIN_ADDRESSES);
    }

    public static void addRemainAddrs(ItemStack ladle, List<String> remainAddrs) {
        List<String> addresses = remainAddrs.subList(0, Math.min(remainAddrs.size(), getMaxAddrCount()));

        if (!addresses.isEmpty()) ladle.set(CMDataComponents.LADLE_REMAIN_ADDRESSES, addresses);
    }

    public static void setNextAddrs(ItemStack ladle) {
        List<String> remainAddrs = ladle.get(CMDataComponents.LADLE_REMAIN_ADDRESSES);
        if (remainAddrs != null && !remainAddrs.isEmpty()) {
            addAddress(ladle, remainAddrs.removeFirst());
            addRemainAddrs(ladle, remainAddrs);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);

        /* Remaining Addresses tooltips */
        if (stack.has(CMDataComponents.LADLE_REMAIN_ADDRESSES)) {
            List<String> remainAddresses = stack.get(CMDataComponents.LADLE_REMAIN_ADDRESSES);
            int skipped = 0;

            if (!remainAddresses.isEmpty()) {
                for (int i = 0; i < remainAddresses.size(); i++) {
                    if (i > 0) {
                        skipped++;
                        continue;
                    }
                    if (!remainAddresses.get(i).isEmpty())
                        tooltipComponents.add(Component.literal("\u00BB " + remainAddresses.get(i)).withStyle(ChatFormatting.GRAY));
                }
                if (skipped > 0)
                    tooltipComponents.add(Component.translatable("container.shulkerBox.more", skipped).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }

        /* Fluid tooltips */
        FluidStack fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
        if (!fluid.isEmpty()) {
            tooltipComponents.add(Component.empty()); // Space
            tooltipComponents.add(fluid.getHoverName().copy().append(" ").append(String.valueOf(fluid.getAmount())).append(CreateLang.translateDirect("generic.unit.millibuckets")).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> open(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack ladle = playerIn.getItemInHand(handIn);
        IFluidHandlerItem fluidContainer = ladle.getCapability(Capabilities.FluidHandler.ITEM);

        if (fluidContainer == null)
            return InteractionResultHolder.pass(ladle);

        if (!playerIn.fireImmune() && worldIn.getRandom().nextInt(10) == 0)
            playerIn.setRemainingFireTicks(MOLTEN_FLUID_BURNING_TIME);

        FluidStack drained = fluidContainer.drain(1000, IFluidHandler.FluidAction.EXECUTE);

        if (drained.getAmount() > 0) {
            if (drained.getAmount() >= 1000) drained.getFluid().getBucket().use(worldIn, playerIn, handIn);

            ParticleOptions fluidParticle = FluidFX.getFluidParticle(drained);
            Vec3 position = playerIn.position();
            if (worldIn.isClientSide()) {
                for (int i = 0; i < 10; i++) {
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, worldIn.getRandom(), .125f);
                    Vec3 pos = position.add(0, .5f, 0).add(playerIn.getLookAngle().scale(.5)).add(motion.scale(4));
                    worldIn.addParticle(fluidParticle, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                }
            }
            //LadleItem.setFluidContents(ladle, fluidContainer); // Update content in hand ladle item
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, ladle);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer().isShiftKeyDown()) {
            return open(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
        }

        Vec3 point = context.getClickLocation();
        float h = style.height() / 16f;
        float r = style.width() / 2f / 16f;

        if (context.getClickedFace() == Direction.DOWN) point = point.subtract(0, h + .25f, 0);
        else if (context.getClickedFace().getAxis().isHorizontal())
            point = point.add(Vec3.atLowerCornerOf(context.getClickedFace().getNormal()).scale(r));

        AABB scanBB = new AABB(point, point).inflate(r, 0, r).expandTowards(0, h, 0);
        Level world = context.getLevel();
        if (!world.getEntities(AllEntityTypes.PACKAGE.get(), scanBB, e -> true).isEmpty()) return super.useOn(context);

        LadleEntity ladleEntity = new LadleEntity(world, point.x, point.y, point.z);
        ItemStack itemInHand = context.getItemInHand();
        ladleEntity.setBox(itemInHand.copy());
        world.addFreshEntity(ladleEntity);
        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticks) {
        if (!(entity instanceof Player player)) return;
        int i = this.getUseDuration(stack, entity) - ticks;
        if (i < 0) return;

        float f = getPackageVelocity(i);
        if (f < 0.1D) return;
        if (world.isClientSide) return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.5F);

        ItemStack copy = stack.copy();
        if (!player.getAbilities().instabuild) stack.shrink(1);

        Vec3 vec = new Vec3(entity.getX(), entity.getY() + entity.getBoundingBox().getYsize() / 2f, entity.getZ());
        Vec3 motion = entity.getLookAngle().scale(f * 2);
        vec = vec.add(motion);

        LadleEntity ladleEntity = new LadleEntity(world, vec.x, vec.y, vec.z);
        ladleEntity.setBox(copy);
        ladleEntity.setDeltaMovement(motion);
        ladleEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(ladleEntity);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "item." + CreateMetallurgy.MOD_ID + (style.rare() ? ".rare_ladle" : ".ladle");
    }

    @Override
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return LadleEntity.fromDroppedItem(world, location, itemstack);
    }
}