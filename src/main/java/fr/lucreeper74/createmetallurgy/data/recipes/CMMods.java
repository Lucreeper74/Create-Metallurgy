package fr.lucreeper74.createmetallurgy.data.recipes;

import com.simibubi.create.api.data.recipe.DatagenMod;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;

public enum CMMods implements DatagenMod {
    CREATE_METALLURGY(CreateMetallurgy.MOD_ID, false),
    TFMG("tfmg", false),
    CADDITION("createaddition", false),
    CUTILITIES("createutilities", false),
    CAVERNS_N_CHASMS("caverns_and_chasms", false)
    ;


    private final String id;
    private boolean invertedMetalPrefix;

    CMMods(String id, boolean invertedMetalPrefix) {
        this.id = id;
        this.invertedMetalPrefix = invertedMetalPrefix;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean reversedMetalPrefix() {
        return invertedMetalPrefix;
    }
}
