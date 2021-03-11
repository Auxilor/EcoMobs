package com.willfp.ecobosses.bosses.util;

import lombok.Getter;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class EffectOption {
    /**
     * The chance of the effect being applied.
     */
    @Getter
    private final double chance;

    /**
     * The level of the effect.
     */
    @Getter
    private final int level;

    /**
     * The potion effect type.
     */
    @Getter
    private final PotionEffectType effectType;

    /**
     * The duration, in ticks.
     */
    @Getter
    private final int duration;

    /**
     * Create a new effect option.
     *
     * @param chance     The chance.
     * @param level      The level.
     * @param duration   The duration in ticks.
     * @param effectType The effect.
     */
    public EffectOption(final double chance,
                        final int level,
                        final int duration,
                        @NotNull final PotionEffectType effectType) {
        this.chance = chance;
        this.level = level;
        this.effectType = effectType;
        this.duration = duration;
    }
}
