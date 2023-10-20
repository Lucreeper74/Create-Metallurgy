package fr.lucreeper74.createmetallurgy.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AllArmInteract extends AllArmInteractionPointTypes {

    public static final FoundryBasinType FOUNDRY_BASIN = register("foundry_basin", FoundryBasinType::new);
    public static final CastingBasinType CASTING_BASIN = register("casting_basin", CastingBasinType::new);
    public static final CastingTableType CASTING_TABLE = register("casting_table", CastingTableType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(CreateMetallurgy.genRL(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static void register() {}

    //

    public static class FoundryBasinType extends ArmInteractionPointType {
        public FoundryBasinType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.FOUNDRY_BASIN_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CastingBasinType extends ArmInteractionPointType {
        public CastingBasinType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.CASTING_BASIN_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CastingTableType extends ArmInteractionPointType {
        public CastingTableType(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.CASTING_TABLE_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }
}
