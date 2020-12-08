package com.willfp.illusioner.illusioner.listeners;

import com.willfp.illusioner.events.entitydeathbyentity.EntityDeathByEntityEvent;
import com.willfp.illusioner.illusioner.IllusionerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DeathListeners implements Listener {
    @EventHandler
    public void onIllusionerDeath(EntityDeathByEntityEvent event) {
        if(!event.getDeathEvent().getEntityType().equals(EntityType.ILLUSIONER))
            return;

        IllusionerManager.OPTIONS.getDeathSounds().forEach(optionedSound -> {
            if(optionedSound.isBroadcast()) {
                event.getKiller().getWorld().playSound(event.getVictim().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
            } else {
                if(event.getKiller() instanceof Player) {
                    ((Player) event.getKiller()).playSound(event.getVictim().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
                }
            }
        });

        IllusionerManager.OPTIONS.getDrops().forEach(drop -> {
            event.getVictim().getLocation().getWorld().dropItemNaturally(event.getVictim().getLocation(), drop);
        });

        event.getDeathEvent().setDroppedExp(IllusionerManager.OPTIONS.generateXp());
    }
}
