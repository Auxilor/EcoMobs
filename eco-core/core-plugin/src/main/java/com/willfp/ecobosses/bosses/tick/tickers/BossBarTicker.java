package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.eco.util.PlayerUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.tick.BossTicker;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
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
        bossBar.name(StringUtils.toComponent(entity.getCustomName()));
        bossBar.progress((float) (entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));

        if (tick % 40 == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerUtils.getAudience(player).hideBossBar(bossBar);
            }
            entity.getNearbyEntities(radius, radius, radius).forEach(entity1 -> {
                if (entity1 instanceof Player) {
                    PlayerUtils.getAudience((Player) entity1).showBossBar(bossBar);
                }
            });
        }
    }

    @Override
    public void onDeath(@NotNull final EcoBoss boss,
                        @Nullable final LivingEntity entity,
                        final long tick) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtils.getAudience(player).hideBossBar(bossBar);
        }
    }
}
