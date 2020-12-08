package com.willfp.illusioner.nms.api;

import org.bukkit.Location;

/**
 * NMS Interface for managing illusioner bosses
 */
public interface IllusionerWrapper {
    EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage);
}