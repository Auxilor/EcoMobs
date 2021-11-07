package com.willfp.ecobosses.events;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.obj.SpawnTotem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EcoBossSpawnTotemEvent extends EcoBossSpawnEvent {

    /**
     * The totem.
     */
    @NotNull
    private final SpawnTotem totem;

    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Default constructor
     *
     * @param boss     - The boss to be spawned.
     * @param player   - The player that spawned this boss (can be null)
     * @param location - The location that boss will spawn at.
     * @param totem - The totem.
     */
    public EcoBossSpawnTotemEvent(@NotNull final EcoBoss boss, @Nullable final Player player, @NotNull final Location location, @NotNull final SpawnTotem totem) {
        super(boss, player, location);
        this.totem = totem;
    }

    @NotNull
    public SpawnTotem getTotem() {
        return this.totem;
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
