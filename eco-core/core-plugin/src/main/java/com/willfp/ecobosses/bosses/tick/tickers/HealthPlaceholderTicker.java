package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class HealthPlaceholderTicker implements BossTicker {
    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        entity.setCustomName(boss.getDisplayName().replace("%health%", StringUtils.internalToString(entity.getHealth())));
    }
}
