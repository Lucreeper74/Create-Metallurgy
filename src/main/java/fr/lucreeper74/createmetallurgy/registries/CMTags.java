package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.createmod.catnip.lang.Lang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMNameSpace.CREATE_METALLURGY;
import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMNameSpace.FORGE;

public class CMTags {

    public enum CMNameSpace {

        CREATE_METALLURGY(CreateMetallurgy.MOD_ID, false, true),
        FORGE("forge"),
        ;

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        CMNameSpace(String id) {
            this(id, true, false);
        }

        CMNameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }

    public enum CMItemTags {

        GRAPHITE_MOLDS(FORGE),
        DIRTY_DUSTS(FORGE),
        WIRES(FORGE),
        LADLE,

        ;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        CMItemTags() {
            this(CREATE_METALLURGY);
        }

        CMItemTags(CMNameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CMItemTags(CMNameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CMItemTags(CMNameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        CMItemTags(CMNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = AllTags.optionalTag(ForgeRegistries.ITEMS, id);
            } else {
                tag = ItemTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }

        private static void init() {
        }
    }

    public static void init() {
        CMItemTags.init();
    }
}
