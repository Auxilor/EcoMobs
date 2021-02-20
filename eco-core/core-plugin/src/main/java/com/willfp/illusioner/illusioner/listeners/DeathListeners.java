package com.willfp.illusioner.illusioner.listeners;


import com.willfp.eco.util.drops.DropQueue;
import com.willfp.eco.util.events.entitydeathbyentity.EntityDeathByEntityEvent;
import com.willfp.illusioner.illusioner.IllusionerManager;
import com.willfp.illusioner.illusioner.OptionedSound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class DeathListeners implements Listener {
    /**
     * Called when the illusioner dies.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void onIllusionerDeath(@NotNull final EntityDeathByEntityEvent event) {
        if (!event.getDeathEvent().getEntityType().equals(EntityType.ILLUSIONER)) {
            return;
        }

        Player player = null;

        if (event.getKiller() instanceof Player) {
            player = (Player) event.getKiller();
        } else if (event.getKiller() instanceof Projectile) {
            if (((Projectile) event.getKiller()).getShooter() instanceof Player) {
                player = (Player) ((Projectile) event.getKiller()).getShooter();
            }
        }

        for (OptionedSound sound : IllusionerManager.OPTIONS.getDeathSounds()) {
            if (sound.isBroadcast()) {
                event.getKiller().getWorld().playSound(event.getVictim().getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
            } else {
                if (player != null) {
                    player.playSound(event.getVictim().getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onOtherDeath(@NotNull final EntityDeathEvent event) {
        event.getDrops().addAll(IllusionerManager.OPTIONS.getDrops());
        event.setDroppedExp(IllusionerManager.OPTIONS.generateXp());
    }
}
