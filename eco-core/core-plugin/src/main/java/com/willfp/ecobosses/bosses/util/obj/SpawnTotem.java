package com.willfp.ecobosses.bosses.util.obj;

import lombok.Data;
import org.bukkit.Material;

@Data
public class SpawnTotem {
    /**
     * The bottom block.
     */
    private final Material bottom;

    /**
     * The middle block.
     */
    private final Material middle;

    /**
     * The top block.
     */
    private final Material top;
}
