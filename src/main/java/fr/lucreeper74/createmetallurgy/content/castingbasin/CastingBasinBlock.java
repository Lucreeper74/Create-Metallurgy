package fr.lucreeper74.createmetallurgy.content.castingbasin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.basin.BasinBlock;
import fr.lucreeper74.createmetallurgy.registries.AllBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.items.ItemHandlerHelper;

public class CastingBasinBlock extends BasinBlock implements IWrenchable {
    public CastingBasinBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }
    @Override
    public BlockEntityType<? extends CastingBasinBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CASTING_BASIN.get();
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!AllBlocks.CASTING_BASIN_BLOCK.has(worldIn.getBlockState(entityIn.blockPosition())))
            return;
        if (!(entityIn instanceof ItemEntity itemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {
            CastingBasinBlockEntity cbe = (CastingBasinBlockEntity) be;
            ItemStack insertItem = ItemHandlerHelper.insertItem(cbe.inputInventory, itemEntity.getItem()
                    .copy(), false);

            if (insertItem.isEmpty()) {
                itemEntity.discard();
                return;
            }

            itemEntity.setItem(insertItem);
        });
    }
}