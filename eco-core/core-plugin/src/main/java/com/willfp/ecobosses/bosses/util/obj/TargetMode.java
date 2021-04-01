package com.willfp.ecobosses.bosses.util.obj;

import com.willfp.eco.util.NumberUtils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TargetMode {
    /**
     * All registered target modes.
     */
    private static final Map<String, TargetMode> VALUES = new HashMap<>();

    /**
     * Random nearby player.
     */
    public static final TargetMode RANDOM = new TargetMode("random", (list, entity) -> {
        return list.get(NumberUtils.randInt(0, list.size() - 1));
    });

    /**
     * Closest player.
     */
    public static final TargetMode CLOSEST = new TargetMode("closest", (list, entity) -> {
        Player player = null;
        double nearestD2 = 10000;
        for (Player nearbyPlayer : list) {
            double d2 = nearbyPlayer.getLocation().distanceSquared(entity.getLocation());
            if (d2 < nearestD2) {
                player = nearbyPlayer;
            }
        }
        return player;
    });

    /**
     * Player with lowest health.
     */
    public static final TargetMode LOWEST_HEALTH = new TargetMode("lowest-health", (list, entity) -> {
        Player player = null;
        double lowest = 100;
        for (Player nearbyPlayer : list) {
            double health = nearbyPlayer.getHealth();
            if (health < lowest) {
                player = nearbyPlayer;
            }
        }
        return player;
    });

    /**
     * Player with highest health.
     */
    public static final TargetMode HIGHEST_HEALTH = new TargetMode("highest-health", (list, entity) -> {
        Player player = null;
        double highest = 0;
        for (Player nearbyPlayer : list) {
            double health = nearbyPlayer.getHealth();
            if (health > highest) {
                player = nearbyPlayer;
            }
        }
        return player;
    });

    /**
     * The name of the target mode.
     */
    @Getter
    private final String name;

    /**
     * The function to find a player out of a list.
     */
    private final BiFunction<List<Player>, LivingEntity, Player> function;

    protected TargetMode(@NotNull final String name,
                         @NotNull final BiFunction<List<Player>, LivingEntity, Player> function) {
        this.name = name;
        this.function = function;

        VALUES.put(name, this);
    }

    /**
     * Get target from list of players.
     *
     * @param list   The list.
     * @param entity The boss.
     * @return The target.
     */
    public Player getTarget(@NotNull final List<Player> list,
                            @NotNull final LivingEntity entity) {
        return function.apply(list, entity);
    }

    /**
     * Get target mode by name.
     *
     * @param name The name.
     * @return The target mode, or null if not found.
     */
    @Nullable
    public static TargetMode getByName(@NotNull final String name) {
        return VALUES.get(name);
    }
}
