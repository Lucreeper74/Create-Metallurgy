//package fr.lucreeper74.createmetallurgy.mixins;
//
//import com.simibubi.create.content.logistics.filter.FilterItemStack;
//import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterItemStack;
//import fr.lucreeper74.createmetallurgy.registries.CMItems;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(value = FilterItemStack.class, remap = false)
//public abstract class FilterItemStackCreationMixin {
//
//    @Shadow
//    private static void trimFilterTag(ItemStack filter) {}
//
//    @Inject(method = "of(Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/content/logistics/filter/FilterItemStack;", at = @At("HEAD"), cancellable = true)
//    private static void of(ItemStack filter, CallbackInfoReturnable<LadleFilterItemStack> info) {
//        if (filter.hasTag() && CMItems.LADLE_FILTER.isIn(filter)) {
//            trimFilterTag(filter);
//            info.setReturnValue(new LadleFilterItemStack(filter));
//        }
//    }
//}