package fr.lucreeper74.createmetallurgy.utils;

import com.simibubi.create.AllSoundEvents;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMPartialModels;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;

public enum SideAttachment implements StringRepresentable {
    NONE,
//    ITEM_PORT(CMItems.ITEM_PORT_ATTACHMENT, SoundEvents.METAL_PLACE, SoundEvents.METAL_BREAK, .5f, 1.2f, CMPartialModels.ITEM_PORT),
//    FLUID_PORT(CMItems.ITEM_PORT_ATTACHMENT, SoundEvents.COPPER_PLACE, SoundEvents.COPPER_BREAK, .5f, 1.2f, CMPartialModels.FLUID_PORT),
    GAUGE(CMItems.GAUGE_ATTACHMENT, AllSoundEvents.WRENCH_ROTATE.getMainEvent(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, .5f, 1f, CMPartialModels.THERMOMETER_GAUGE);

    private final ItemLike item;
    private final SoundEvent addSound;
    private final SoundEvent removeSound;
    private final float volume;
    private final float pitch;
    private final PartialModel model;

    SideAttachment() {
        this(null, null, null, 0f, 0f, null);
    }

    SideAttachment(ItemLike item, SoundEvent addSound, SoundEvent removeSound, float volume, float pitch, PartialModel model) {
        this.item = item;
        this.addSound = addSound;
        this.removeSound = removeSound;
        this.volume = volume;
        this.pitch = pitch;
        this.model = model;
    }

    public ItemStack getItem() {
        if (item == null)
            return ItemStack.EMPTY;

        return new ItemStack(item);
    }

    public PartialModel getModel() {
        return model;
    }

    // to avoid unnecessary array copy
    private static final SideAttachment[] values = values();

    @Override
    public String getSerializedName() {
        return CMLang.asId(name());
    }

    public static SideAttachment byName(String name) {
        for (SideAttachment mode : values) {
            if (mode.getSerializedName().equals(name))
                return mode;
        }
        return NONE;
    }

    public void onAdd(UseOnContext context) {
        if (addSound == null)
            return;

        context.getLevel().playSound(null, context.getClickedPos(),
                addSound, SoundSource.PLAYERS, volume, pitch);
    }

    public void onRemove(UseOnContext context) {
        if (removeSound == null)
            return;

        context.getLevel().playSound(null, context.getClickedPos(),
                removeSound, SoundSource.PLAYERS, volume, pitch);
    }
}