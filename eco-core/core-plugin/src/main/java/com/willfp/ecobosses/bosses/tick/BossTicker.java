package com.willfp.ecobosses.bosses.tick;

import com.willfp.ecobosses.bosses.EcoBoss;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface BossTicker {
    /**
     * Run on boss tick.
     *
     * @param boss   The boss.
     * @param entity The boss entity.
     * @param tick   The current tick: counts up from zero.
     */
    void tick(@NotNull EcoBoss boss,
              @NotNull LivingEntity entity,
              long tick);

    /**
     * Run on boss death.
     *
     * @param boss   The boss.
     * @param entity The boss entity.
     * @param tick   The current tick: counts up from zero.
     */
    default void onDeath(@NotNull EcoBoss boss,
                         @NotNull LivingEntity entity,
                         long tick) {
        // Can be overridden when needed.
    }
}
