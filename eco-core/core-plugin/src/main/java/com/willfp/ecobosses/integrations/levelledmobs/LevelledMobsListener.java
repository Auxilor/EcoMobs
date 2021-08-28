package com.willfp.ecobosses.integrations.levelledmobs;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import me.lokka30.levelledmobs.events.MobPreLevelEvent;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class LevelledMobsListener implements Listener {
    @EventHandler
    public void onMobLevel(@NotNull final MobPreLevelEvent event) {
        EcoBoss boss = BossUtils.getBoss(event.getEntity());

        if (boss == null) {
            return;
        }

        event.setCancelled(true);
    }
}
