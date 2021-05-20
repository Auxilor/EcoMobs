package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BossBarTicker implements BossTicker {
    /**
     * The boss bar.
     */
    private final BossBar bossBar;

    /**
     * The radius that the boss bar should be visible in.
     */
    private final double radius;

    /**
     * Create new boss bar ticker.
     * @param bossBar The boss bar.
     * @param radius The radius.
     */
    public BossBarTicker(@NotNull final BossBar bossBar,
                         final double radius) {
        this.bossBar = bossBar;
        this.radius = radius;
    }

    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        bossBar.setTitle(entity.getCustomName());
        bossBar.setProgress(entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        if (tick % 40 == 0) {
            bossBar.removeAll();
            entity.getNearbyEntities(radius, radius, radius).forEach(entity1 -> {
                if (entity1 instanceof Player) {
                    bossBar.addPlayer((Player) entity1);
                }
            });
        }
    }

    @Override
    public void onDeath(@NotNull final EcoBoss boss,
                        @Nullable final LivingEntity entity,
                        final long tick) {
        bossBar.removeAll();
        bossBar.setVisible(false);
        Bukkit.removeBossBar(((KeyedBossBar) bossBar).getKey());
    }
}
