package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import org.bukkit.Bukkit;
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

            for (String despawnMessage : boss.getDespawnMessages()) {
                Bukkit.broadcastMessage(despawnMessage);
            }

            for (OptionedSound sound : boss.getDespawnSounds()) {
                entity.getWorld().playSound(entity.getLocation(), sound.sound(), sound.volume(), sound.pitch());
            }
        }
    }
}
