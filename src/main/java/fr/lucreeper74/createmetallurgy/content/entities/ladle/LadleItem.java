package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleFluidHandler.LADLE_CAPACITY;

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

    public static boolean isLadle(ItemStack stack) {
        return stack.getItem() instanceof LadleItem;
    }

    public static void clearRemainAddrs(ItemStack ladle) {
        if (ladle.hasTag())
            ladle.getTag().remove("RemainAddrs");
    }

    public static void addRemainAddrs(ItemStack ladle, ArrayList<String> remainAddrs) {
        ListTag list = new ListTag();
        for (String address : remainAddrs) {
            if (address != null) {
                CompoundTag addressTag = new CompoundTag();
                addressTag.putString("Address", address);
                list.add(addressTag);
            }
        }
        if (!list.isEmpty())
            ladle.getOrCreateTag().put("RemainAddrs", list);
    }

    public static void setNextAddrs(ItemStack ladle) {
        if (ladle.hasTag()) {
            CompoundTag tag = ladle.getOrCreateTag();
            if (tag.contains("RemainAddrs")) {
                ListTag list = tag.getList("RemainAddrs", Tag.TAG_COMPOUND);
                if (!list.isEmpty()) {
                    tag.putString("Address", list.getCompound(0).getString("Address"));
                    list.remove(0);
                    tag.put("RemainAddrs", list);
                }
            }
        }
    }

    public static FluidTank getFluidContents(ItemStack ladle) {
        FluidTank newTank = new FluidTank(LADLE_CAPACITY);
        CompoundTag fluidNBT = ladle.getTagElement("Fluid");
        if (fluidNBT != null && !fluidNBT.isEmpty())
            newTank.readFromNBT(fluidNBT);
        return newTank;
    }

    public static void setFluidContents(ItemStack ladle, FluidTank fluidTank) {
        if (!fluidTank.isEmpty())
            ladle.getOrCreateTag()
                    .put("Fluid", fluidTank.writeToNBT(new CompoundTag()));
        else
            ladle.removeTagKey("Fluid");
    }

    public static int getFluidAmount(ItemStack ladle) {
        return LadleItem.getFluidContents(ladle).getFluidAmount();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        CompoundTag nbt = stack.getOrCreateTag();

        /* Remaining Addresses tooltips */
        if (nbt.contains("RemainAddrs", Tag.TAG_LIST)) {
            ListTag remainAddresses = nbt.getList("RemainAddrs", Tag.TAG_COMPOUND);
            int skipped = 0;

            if (!remainAddresses.isEmpty()) {
                for (int i = 0; i < remainAddresses.size(); i++) {
                    if (i > 0) {
                        skipped++;
                        continue;
                    }
                    CompoundTag addressTag = remainAddresses.getCompound(i);
                    if (addressTag.contains("Address", Tag.TAG_STRING))
                        tooltipComponents.add(Component.literal("\u00BB " + addressTag.getString("Address"))
                                .withStyle(ChatFormatting.GRAY));
                }
                if (skipped > 0)
                    tooltipComponents.add(Component.translatable("container.shulkerBox.more", skipped)
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }

        /* Fluid tooltips */
        FluidStack fluid = getFluidContents(stack).getFluidInTank(0);
        if (!fluid.isEmpty()) {
            tooltipComponents.add(Component.empty()); // Space
            tooltipComponents.add(fluid.getDisplayName()
                    .copy()
                    .append(" ")
                    .append(String.valueOf(fluid.getAmount()))
                    .append(CreateLang.translateDirect("generic.unit.millibuckets"))
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> open(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack ladle = playerIn.getItemInHand(handIn);
        FluidTank fluidContainer = getFluidContents(ladle);

        if (!fluidContainer.isEmpty()) {
            FluidStack drained = fluidContainer.drain(1000, IFluidHandler.FluidAction.EXECUTE);

            if (drained.getAmount() > 0) {
                if (drained.getAmount() >= 1000)
                    drained.getFluid().getBucket().use(worldIn, playerIn, handIn);

                ParticleOptions fluidParticle = FluidFX.getFluidParticle(drained);
                Vec3 position = playerIn.position();
                AllSoundEvents.STEAM.playOnServer(worldIn, playerIn.blockPosition()); // Todo: change the sound
                if (worldIn.isClientSide()) {
                    for (int i = 0; i < 10; i++) {
                        Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, worldIn.getRandom(), .125f);
                        Vec3 pos = position.add(0, .5f, 0)
                                .add(playerIn.getLookAngle()
                                        .scale(.5))
                                .add(motion.scale(4));
                        worldIn.addParticle(fluidParticle, pos.x, pos.y, pos.z, motion.x,
                                motion.y, motion.z);
                    }
                }
                LadleItem.setFluidContents(ladle, fluidContainer); // Update content in hand ladle item
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, ladle);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer()
                .isShiftKeyDown()) {
            return open(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
        }

        Vec3 point = context.getClickLocation();
        float h = style.height() / 16f;
        float r = style.width() / 2f / 16f;

        if (context.getClickedFace() == Direction.DOWN)
            point = point.subtract(0, h + .25f, 0);
        else if (context.getClickedFace()
                .getAxis()
                .isHorizontal())
            point = point.add(Vec3.atLowerCornerOf(context.getClickedFace()
                            .getNormal())
                    .scale(r));

        AABB scanBB = new AABB(point, point).inflate(r, 0, r)
                .expandTowards(0, h, 0);
        Level world = context.getLevel();
        if (!world.getEntities(AllEntityTypes.PACKAGE.get(), scanBB, e -> true)
                .isEmpty())
            return super.useOn(context);

        LadleEntity ladleEntity = new LadleEntity(world, point.x, point.y, point.z);
        ItemStack itemInHand = context.getItemInHand();
        ladleEntity.setBox(itemInHand.copy());
        world.addFreshEntity(ladleEntity);
        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticks) {
        if (!(entity instanceof Player player))
            return;
        int i = this.getUseDuration(stack) - ticks;
        if (i < 0)
            return;

        float f = getPackageVelocity(i);
        if (f < 0.1D)
            return;
        if (world.isClientSide)
            return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW,
                SoundSource.NEUTRAL, 0.5F, 0.5F);

        ItemStack copy = stack.copy();
        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        Vec3 vec = new Vec3(entity.getX(), entity.getY() + entity.getBoundingBox()
                .getYsize() / 2f, entity.getZ());
        Vec3 motion = entity.getLookAngle()
                .scale(f * 2);
        vec = vec.add(motion);

        LadleEntity ladleEntity = new LadleEntity(world, vec.x, vec.y, vec.z);
        ladleEntity.setBox(copy);
        ladleEntity.setDeltaMovement(motion);
        ladleEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(ladleEntity);
    }

    @Override
    public String getDescriptionId() {
        return "item." + CreateMetallurgy.MOD_ID + (style.rare() ? ".rare_ladle" : ".ladle");
    }

    @Override
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return LadleEntity.fromDroppedItem(world, location, itemstack);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (this.getClass() == LadleItem.class)
            return new LadleFluidHandler(stack);
        else
            return super.initCapabilities(stack, nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new LadleItemRenderer()));
        super.initializeClient(consumer);
    }
}