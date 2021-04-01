package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TargetTicker implements BossTicker {
    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        if (tick % 10 == 0) {
            for (Entity nearbyEntity : entity.getNearbyEntities(10, 5, 10)) {
                if (nearbyEntity instanceof Player && entity instanceof Mob) {
                    ((Mob) entity).setTarget((Player) nearbyEntity);
                }
            }
        }
    }
}
