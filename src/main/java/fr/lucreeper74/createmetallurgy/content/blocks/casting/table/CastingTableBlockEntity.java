package fr.lucreeper74.createmetallurgy.content.blocks.casting.table;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMBlockEntityTypes;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CastingTableBlockEntity extends CastingBlockEntity implements IHaveGoggleInformation {

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.CASTING_BASIN.get(),
                (be, context) -> be.itemCapability
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMBlockEntityTypes.CASTING_BASIN.get(),
                (be, context) -> be.getFluidTank()
        );
    }

    @Override
    protected void playProcessSound() {
        float pitch = 2f - level.getRandom().nextFloat() * .4f;

        level.playSound(null, worldPosition,
                SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, .5f, pitch);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == CMRecipeTypes.CASTING_IN_TABLE.getType();
    }

    private static final Object CastingInTableRecipesKey = new Object();

    @Override
    protected Object getRecipeCacheKey() {
        return CastingInTableRecipesKey;
    }

    // Client Stuff
    @Override
    public LangBuilder getGoggleTooltip() {
        return CMLang.translate("gui.goggles.castingtable_contents");
    }
}