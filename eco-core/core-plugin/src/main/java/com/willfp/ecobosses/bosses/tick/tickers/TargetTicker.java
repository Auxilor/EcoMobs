package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import com.willfp.ecobosses.bosses.util.obj.TargetMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TargetTicker implements BossTicker {
    /**
     * The targeting mode.
     */
    private final TargetMode mode;

    /**
     * The maximum range.
     */
    private final double range;

    /**
     * Create new target ticker.
     *
     * @param mode  The targeting mode.
     * @param range The range.
     */
    public TargetTicker(@NotNull final TargetMode mode,
                        final double range) {
        this.mode = mode;
        this.range = range;
    }

    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        Mob mob = (Mob) entity;
        if (tick % 10 == 0) {
            List<Player> nearbyPlayers = new ArrayList<>();
            for (Entity nearbyEntity : entity.getNearbyEntities(range, range, range)) {
                if (nearbyEntity instanceof Player) {
                    nearbyPlayers.add((Player) nearbyEntity);
                }
            }

            if (nearbyPlayers.isEmpty()) {
                return;
            }

            mob.setTarget(mode.getTarget(nearbyPlayers, entity));
        }
    }
}
