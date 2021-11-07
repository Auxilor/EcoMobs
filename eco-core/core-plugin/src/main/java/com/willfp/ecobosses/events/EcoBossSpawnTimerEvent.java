package com.willfp.ecobosses.events;

import com.willfp.ecobosses.bosses.EcoBoss;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EcoBossSpawnTimerEvent extends EcoBossSpawnEvent {
    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Default constructor
     *
     * @param boss     - The boss to be spawned.
     * @param location - The location that boss will spawn at.
     */
    public EcoBossSpawnTimerEvent(@NotNull EcoBoss boss, @NotNull Location location) {
        super(boss, null, location);
    }

    /**
     * Bukkit parity.
     *
     * @return The handlers.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Bukkit parity.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
