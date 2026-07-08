package fr.lucreeper74.createmetallurgy.content.blocks.casting.basin;

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

public class CastingBasinBlockEntity extends CastingBlockEntity {

    public CastingBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CMBlockEntityTypes.CASTING_TABLE.get(),
                (be, context) -> be.itemCapability
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CMBlockEntityTypes.CASTING_TABLE.get(),
                (be, context) -> be.getFluidTank()
        );
    }

    @Override
    protected void playProcessSound() {
        float pitch = 1f - level.getRandom().nextFloat() * .4f;

        level.playSound(null, worldPosition,
                SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, .5f, pitch);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == CMRecipeTypes.CASTING_IN_BASIN.getType();
    }

    private static final Object CastingInBasinRecipesKey = new Object();

    @Override
    protected Object getRecipeCacheKey() {
        return CastingInBasinRecipesKey;
    }

    // Client Stuff
    @Override
    public LangBuilder getGoggleTooltip() {
        return CMLang.translate("gui.goggles.castingbasin_contents");
    }
}