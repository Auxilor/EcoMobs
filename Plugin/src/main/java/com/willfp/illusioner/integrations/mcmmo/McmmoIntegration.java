package com.willfp.illusioner.integrations.mcmmo;

import com.willfp.illusioner.integrations.Integration;
import org.bukkit.event.Event;

/**
 * Interface for mcMMO integrations
 */
public interface McmmoIntegration extends Integration {
    /**
     * @see McmmoManager#isFake(Event)
     */
    boolean isFake(Event event);
}
