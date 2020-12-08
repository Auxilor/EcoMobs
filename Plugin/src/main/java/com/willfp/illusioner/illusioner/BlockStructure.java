package com.willfp.illusioner.illusioner;

import org.bukkit.Material;

public class BlockStructure {
    private final Material bottom;
    private final Material middle;
    private final Material top;

    public BlockStructure(Material bottom, Material middle, Material top) {
        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
    }

    public Material getBottom() {
        return bottom;
    }

    public Material getMiddle() {
        return middle;
    }

    public Material getTop() {
        return top;
    }

    public static boolean matches(BlockStructure structure) {
        return structure.getBottom().equals(IllusionerManager.OPTIONS.getSpawnStructure().getBottom())
                && structure.getMiddle().equals(IllusionerManager.OPTIONS.getSpawnStructure().getMiddle())
                && structure.getTop().equals(IllusionerManager.OPTIONS.getSpawnStructure().getTop());
    }
}
