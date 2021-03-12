package com.willfp.ecobosses.bosses.util.obj;

import com.willfp.eco.util.NumberUtils;

public class ExperienceOptions {
    /**
     * The minimum xp to drop.
     */
    private final int minimum;

    /**
     * The maximum xp to drop.
     */
    private final int maximum;

    /**
     * Create new experience options.
     *
     * @param minimum Minimum xp.
     * @param maximum Maximum xp.
     */
    public ExperienceOptions(final int minimum,
                             final int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * Generate an exp amount.
     *
     * @return The amount.
     */
    public int generateXp() {
        return NumberUtils.randInt(minimum, maximum);
    }
}
