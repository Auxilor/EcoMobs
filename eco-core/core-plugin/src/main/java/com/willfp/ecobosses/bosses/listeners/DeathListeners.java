package com.willfp.ecobosses.bosses.listeners;


import com.willfp.eco.util.events.entitydeathbyentity.EntityDeathByEntityEvent;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class DeathListeners implements Listener {
    /**
     * Called when a boss dies.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void onBossDeath(@NotNull final EntityDeathByEntityEvent event) {
        LivingEntity entity = event.getVictim();

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        for (OptionedSound sound : boss.getDeathSounds()) {
            entity.getWorld().playSound(entity.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }

    /**
     * Handle drops and experience.
     *
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onOtherDeath(@NotNull final EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        event.getDrops().addAll(boss.getDrops());
        event.setDroppedExp(boss.getExperienceOptions().generateXp());
    }
}
