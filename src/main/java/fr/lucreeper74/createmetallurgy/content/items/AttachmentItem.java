package fr.lucreeper74.createmetallurgy.content.items;

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.utils.SideAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class AttachmentItem extends Item {

    public AttachmentItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (level.getBlockEntity(clickedPos) instanceof CrucibleBlockEntity be) {
            Direction clickedSide = context.getClickedFace();

            if (be.setAttachment(clickedSide, getSideAttachment(), false)) {
                getSideAttachment().onAdd(context);

                    if (!context.getPlayer().isCreative())
                        if (!getSideAttachment().getItem().isEmpty())
                            context.getPlayer().getMainHandItem().shrink(1);

                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    public abstract SideAttachment getSideAttachment();


//    public static class ItemPortAttachmentItem extends AttachmentItem {
//        public ItemPortAttachmentItem(Properties properties) {
//            super(properties);
//        }
//
//        @Override
//        public SideAttachment getSideAttachment() {
//            return SideAttachment.ITEM_PORT;
//        }
//    }

//    public static class FluidPortAttachmentItem extends AttachmentItem {
//        public FluidPortAttachmentItem(Properties properties) {
//            super(properties);
//        }
//
//        @Override
//        public SideAttachment getSideAttachment() {
//            return SideAttachment.FLUID_PORT;
//        }
//    }


    public static class GaugeAttachmentItem extends AttachmentItem {
        public GaugeAttachmentItem(Properties properties) {
            super(properties);
        }

        @Override
        public SideAttachment getSideAttachment() {
            return SideAttachment.GAUGE;
        }
    }
}
