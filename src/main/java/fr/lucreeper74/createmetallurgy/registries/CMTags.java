package fr.lucreeper74.createmetallurgy.registries;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMNameSpace.CREATE_METALLURGY;
import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMNameSpace.FORGE;

public class CMTags {

    public enum CMNameSpace {

        CREATE_METALLURGY(CreateMetallurgy.MOD_ID),
        FORGE("forge"),
        ;

        public final String id;

        CMNameSpace(String id) {
            this.id = id;
        }

        public ResourceLocation id(String path) {
            return new ResourceLocation(this.id, path);
        }

        public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
            return this.id(pathOverride != null ? pathOverride : Lang.asId(entry.name()));
        }
    }

    public enum CMBlockTags {

        COKE_STORAGE_BLOCKS(FORGE, "storage_blocks/coke_block"),
        LIGHT_BULB,

        ;

        public final TagKey<Block> tag;

        CMBlockTags() {
            this(CREATE_METALLURGY);
        }

        CMBlockTags(CMNameSpace namespace) {
            this(namespace, null);
        }

        CMBlockTags(CMNameSpace namespace, @Nullable String pathOverride) {
            this.tag = TagKey.create(Registries.BLOCK, namespace.id(this, pathOverride));
        }

        public boolean matches(BlockState state) {
            return state.is(tag);
        }
    }

    public enum CMItemTags {

        GRAPHITE_MOLDS,
        GRAPHITE(FORGE),
        DIRTY_DUSTS(FORGE),
        WIRES(FORGE),
        LADLE,
        COKE_STORAGE_BLOCKS(FORGE, "storage_blocks/coke_block"),
        LIGHT_BULB,
        COAL_COKE(FORGE),
        SLAG,

        ;

        public final TagKey<Item> tag;

        CMItemTags() {
            this(CREATE_METALLURGY);
        }

        CMItemTags(CMNameSpace namespace) {
            this(namespace, null);
        }

        CMItemTags(CMNameSpace namespace, @Nullable String pathOverride) {
            this.tag = TagKey.create(Registries.ITEM, namespace.id(this, pathOverride));
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }
    }

    public enum CMFluidTags {

        MOLTEN_MATERIAL(FORGE),
        ;

        public final TagKey<Fluid> tag;

        CMFluidTags() {
            this(CREATE_METALLURGY);
        }

        CMFluidTags(CMNameSpace namespace) {
            this(namespace, null);
        }

        CMFluidTags(CMNameSpace namespace, @Nullable String pathOverride) {
            this.tag = TagKey.create(Registries.FLUID, namespace.id(this, pathOverride));
        }
    }
}
