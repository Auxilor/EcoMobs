package com.willfp.ecobosses.bosses.util.obj;

import com.willfp.ecobosses.bosses.util.bosstype.BossType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class SummonsOption {
    /**
     * The chance of a mob being spawned.
     */
    @Getter
    private final double chance;

    /**
     * The type of entity to summon.
     */
    @Getter
    private final BossType type;

    /**
     * Create a new summons option.
     *
     * @param chance The chance.
     * @param type   The entity type.
     */
    public SummonsOption(final double chance,
                         @NotNull final BossType type) {
        this.chance = chance;
        this.type = type;
    }
}
