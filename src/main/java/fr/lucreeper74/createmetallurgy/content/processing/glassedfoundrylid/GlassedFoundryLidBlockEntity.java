package fr.lucreeper74.createmetallurgy.content.processing.glassedfoundrylid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GlassedFoundryLidBlockEntity extends BlockEntity {
    public GlassedFoundryLidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}