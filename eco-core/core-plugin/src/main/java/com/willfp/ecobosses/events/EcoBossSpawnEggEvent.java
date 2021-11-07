package com.willfp.ecobosses.events;

import com.willfp.ecobosses.bosses.EcoBoss;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EcoBossSpawnEggEvent extends EcoBossSpawnEvent {
    /**
     * The egg item.
     */
    @NotNull
    private final ItemStack egg;

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
     * @param egg - ItemStack that represents the egg.
     */
    public EcoBossSpawnEggEvent(@NotNull EcoBoss boss, @Nullable Player player, @NotNull Location location, @NotNull ItemStack egg) {
        super(boss, player, location);
        this.egg = egg;
    }

    @NotNull
    public final ItemStack getEgg() {
        return this.egg;
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
