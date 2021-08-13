package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DeathTimeTicker implements BossTicker {
    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        if (boss.getTimeToLive() < 0) {
            return;
        }

        int timeLeft;
        timeLeft = (int) (entity.getMetadata("death-time").get(0).asLong() - System.currentTimeMillis());
        timeLeft = (int) Math.ceil(timeLeft / 1000D);
        if (timeLeft <= 0) {
            entity.remove();
            boss.removeLivingBoss(entity);
        }
    }
}
