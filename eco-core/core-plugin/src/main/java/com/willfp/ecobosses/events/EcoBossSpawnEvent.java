package com.willfp.ecobosses.events;

import com.willfp.ecobosses.bosses.EcoBoss;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents all boss spawn events fired by EcoBosses.
 */
public class EcoBossSpawnEvent extends Event implements Cancellable {
    /**
     * Event cancellation state.
     */
    private boolean cancelled;

    /**
     * The boss to be spawned.
     */
    private final EcoBoss boss;

    /**
     * Location that boss will be spawned at.
     */
    private final Location location;

    /**
     * Player that spawned this boss.
     * (Will be null if EcoBossSpawnTimerEvent is fired)
     */
    @Nullable
    private final Player player;

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     *
     * Default constructor
     *
     * @param boss - The boss to be spawned.
     * @param player - The player that spawned this boss (can be null)
     * @param location - The location that boss will spawn at.
     */
    public EcoBossSpawnEvent(@NotNull final EcoBoss boss, @Nullable final Player player, @NotNull final Location location) {
        this.cancelled = false;
        this.boss = boss;
        this.location = location;
        this.player = player;
    }

    /**
     *
     * Get the EcoBoss.
     *
     * @return - EcoBoss to be spawned.
     */
    @NotNull
    public EcoBoss getBoss() {
        return this.boss;
    }

    /**
     *
     * Get the location.
     *
     * @return - Location.
     */
    @NotNull
    public Location getLocation() {
        return this.location;
    }

    /**
     *
     * Get the player if present.
     *
     * @return - The player.
     */
    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    /**
     *
     * Check if this event has any player presented.
     *
     * @return - If any player is presented in this event.
     */
    public boolean hasPlayer() {
        return this.player != null;
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
     *
     * Get if event is cancelled.
     *
     * @return Event cancellation state.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     *
     * Set event cancellation state.
     *
     * @param b - The state to set.
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
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
