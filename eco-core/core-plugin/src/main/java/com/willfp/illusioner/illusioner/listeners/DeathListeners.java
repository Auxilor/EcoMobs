package com.willfp.illusioner.illusioner.listeners;


import com.willfp.eco.util.drops.DropQueue;
import com.willfp.eco.util.events.entitydeathbyentity.EntityDeathByEntityEvent;
import com.willfp.illusioner.illusioner.IllusionerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        IllusionerManager.OPTIONS.getDeathSounds().forEach(optionedSound -> {
            if (optionedSound.isBroadcast()) {
                event.getKiller().getWorld().playSound(event.getVictim().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
            } else {
                if (event.getKiller() instanceof Player) {
                    ((Player) event.getKiller()).playSound(event.getVictim().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
                }
            }
        });

        if (event.getKiller() instanceof Player) {
            new DropQueue((Player) event.getKiller())
                    .addItems(IllusionerManager.OPTIONS.getDrops())
                    .addXP(IllusionerManager.OPTIONS.generateXp())
                    .setLocation(event.getVictim().getLocation())
                    .push();
        }


        event.getDeathEvent().setDroppedExp(0);
    }
}
