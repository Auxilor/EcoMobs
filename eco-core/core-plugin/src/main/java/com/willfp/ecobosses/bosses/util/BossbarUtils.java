package com.willfp.ecobosses.bosses.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BossbarUtils {
    public BossBar createBossBar(@NotNull final LivingEntity entity) {
        if (bossBar != null) {
            return bossBar;
        }
        BossBar bossBar = Bukkit.getServer().createBossBar(this.displayName, IllusionerManager.OPTIONS.getColor(), IllusionerManager.OPTIONS.getStyle(), (BarFlag) null);
        this.bossBar = bossBar;

        LivingEntity entity = (LivingEntity) this.getBukkitEntity();

        plugin.getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                entity.getNearbyEntities(50, 50, 50).forEach(entity1 -> {
                    if (entity1 instanceof Player) {
                        bossBar.addPlayer((Player) entity1);
                    }
                });
            } else {
                runnable.cancel();
            }
        }).runTaskTimer(0, 40);

        plugin.getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.setProgress(entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            } else {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                bossBar.setVisible(false);
                runnable.cancel();
            }
        }).runTaskTimer(0, 1);

        return bossBar;
    }
}
