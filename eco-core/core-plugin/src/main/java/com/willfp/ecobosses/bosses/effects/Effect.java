package com.willfp.ecobosses.bosses.effects;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Effect implements BossTicker {
    /**
     * The effect args.
     */
    @Getter
    private final List<String> args;

    /**
     * Create a new effect.
     *
     * @param args The args.
     */
    protected Effect(@NotNull final List<String> args) {
        this.args = args;
    }

    /**
     * Show a config error.
     *
     * @param message The error message.
     */
    public void showConfigError(@NotNull final String message) {
        Bukkit.getLogger().warning("An effect is configured incorrectly!");
        Bukkit.getLogger().warning(message);
        Bukkit.getLogger().warning("Usage: " + getUsage());
    }

    /**
     * Get effect usage.
     *
     * @return The usage.
     */
    public abstract String getUsage();

    /**
     * Handle the boss attacking a player.
     *
     * @param boss   The boss.
     * @param entity The boss entity.
     * @param player The player.
     */
    public void onAttack(@NotNull final EcoBoss boss,
                         @NotNull final LivingEntity entity,
                         @NotNull final Player player) {
        // Override when needed.
    }

    /**
     * Tick the effect.
     *
     * @param boss   The boss.
     * @param entity The boss entity.
     * @param tick   The current tick: counts up from zero.
     */
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        // Override when needed.
    }

    /**
     * On boss death.
     *
     * @param boss   The boss.
     * @param entity The boss entity.
     * @param tick   The current tick: counts up from zero.
     */
    @Override
    public void onDeath(@NotNull final EcoBoss boss,
                        @NotNull final LivingEntity entity,
                        final long tick) {
        // Override when needed.
    }
}
