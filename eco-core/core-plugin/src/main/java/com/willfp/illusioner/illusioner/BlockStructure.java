package com.willfp.illusioner.illusioner;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class BlockStructure {
    /**
     * The bottom block.
     */
    @Getter
    private final Material bottom;

    /**
     * The middle block.
     */
    @Getter
    private final Material middle;

    /**
     * The top block.
     */
    @Getter
    private final Material top;

    /**
     * Create a new block structure.
     *
     * @param bottom The bottom block.
     * @param middle The middle block.
     * @param top    The top block.
     */
    public BlockStructure(@NotNull final Material bottom,
                          @NotNull final Material middle,
                          @NotNull final Material top) {
        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
    }

    /**
     * If a block structure matches the specified structure in illusioner options.
     *
     * @param structure The block structure to test against.
     * @return If the structures match.
     */
    public static boolean matches(@NotNull final BlockStructure structure) {
        return structure.getBottom().equals(IllusionerManager.OPTIONS.getSpawnStructure().getBottom())
                && structure.getMiddle().equals(IllusionerManager.OPTIONS.getSpawnStructure().getMiddle())
                && structure.getTop().equals(IllusionerManager.OPTIONS.getSpawnStructure().getTop());
    }
}
