package com.willfp.illusioner.nms.api;

import org.bukkit.entity.Player;

/**
 * NMS Interface for getting attack cooldown
 */
public interface CooldownWrapper {
    double getAttackCooldown(Player player);
}
