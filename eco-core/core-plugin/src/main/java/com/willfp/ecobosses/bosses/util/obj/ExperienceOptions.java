package com.willfp.ecobosses.bosses.util.obj;

import com.willfp.eco.util.NumberUtils;

public record ExperienceOptions(int minimum, int maximum) {
    /**
     * Generate an exp amount.
     *
     * @return The amount.
     */
    public int generateXp() {
        return NumberUtils.randInt(minimum, maximum);
    }
}
