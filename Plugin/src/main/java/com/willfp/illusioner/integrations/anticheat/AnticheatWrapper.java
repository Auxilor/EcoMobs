package com.willfp.illusioner.integrations.anticheat;

import com.willfp.illusioner.integrations.Integration;
import org.bukkit.entity.Player;

/**
 * Interface for anticheat integrations
 */
public interface AnticheatWrapper extends Integration {
    /**
     * Exempt a player from checks
     *
     * @param player The player to exempt
     */
    void exempt(Player player);

    /**
     * Unexempt a player from checks
     *
     * @param player The player to unexempt
     */
    void unexempt(Player player);
}
