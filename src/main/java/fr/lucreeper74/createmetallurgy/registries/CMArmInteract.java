package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CMArmInteract extends AllArmInteractionPointTypes {

    public static final FoundryBasinType FOUNDRY_BASIN = register("foundry_basin", FoundryBasinType::new);
    public static final CastingBasinType CASTING_BASIN = register("casting_basin", CastingBasinType::new);
    public static final CastingTableType CASTING_TABLE = register("casting_table", CastingTableType::new);
    public static final BeltGrinderType BELT_GRINDER = register("belt_grinder", BeltGrinderType::new);

    private static <T extends ArmInteractionPointType> T register(String id, Supplier<T> factory) {
        T type = factory.get();
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateMetallurgy.genRL(id), type);
        return type;
    }

    public static void register() {}

    //

    public static class FoundryBasinType extends ArmInteractionPointType {
        public FoundryBasinType() {}

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CMBlocks.FOUNDRY_BASIN_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CastingBasinType extends ArmInteractionPointType {


        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CMBlocks.CASTING_BASIN_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CastingTableType extends ArmInteractionPointType {


        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CMBlocks.CASTING_TABLE_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class BeltGrinderType extends ArmInteractionPointType {


        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CMBlocks.BELT_GRINDER_BLOCK.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }
}
