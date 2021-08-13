package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class NamePlaceholderTicker implements BossTicker {
    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        int timeLeft = -1;
        if (boss.getTimeToLive() > 0) {
            timeLeft = (int) (entity.getMetadata("death-time").get(0).asLong() - System.currentTimeMillis());
            timeLeft = (int) Math.ceil(timeLeft / 1000D);
        }

        String time = "";
        if (timeLeft > 0) {
            time = String.format("%d:%02d", timeLeft/60, timeLeft%60);
        }
        entity.setCustomName(
                boss.getDisplayName()
                        .replace("%health%", StringUtils.internalToString(entity.getHealth()))
                        .replace("%time%", time)
        );
    }
}
