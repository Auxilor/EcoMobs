package com.willfp.ecobosses.bosses.util;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpawnTotem {
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
     * Create a new spawn totem.
     *
     * @param bottom The bottom block.
     * @param middle The middle block.
     * @param top    The top block.
     */
    public SpawnTotem(@NotNull final Material bottom,
                      @NotNull final Material middle,
                      @NotNull final Material top) {
        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpawnTotem)) {
            return false;
        }
        SpawnTotem that = (SpawnTotem) o;
        return getBottom() == that.getBottom() && getMiddle() == that.getMiddle() && getTop() == that.getTop();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBottom(), getMiddle(), getTop());
    }
}
