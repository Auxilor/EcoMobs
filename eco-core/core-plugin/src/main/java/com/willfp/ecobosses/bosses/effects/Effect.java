package com.willfp.ecobosses.bosses.effects;

import com.willfp.ecobosses.bosses.tick.BossTicker;
import lombok.Getter;
import org.bukkit.Bukkit;
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

    public void showConfigError(@NotNull final String message) {
        Bukkit.getLogger().warning("An effect is configured incorrectly!");
        Bukkit.getLogger().warning(message);
        Bukkit.getLogger().warning("Usage: " + getUsage());
    }

    public abstract String getUsage();
}
