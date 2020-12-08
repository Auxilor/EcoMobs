package com.willfp.illusioner.illusioner;

import com.willfp.illusioner.util.NumberUtils;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListeners implements Listener {
    @EventHandler
    public void onIllusionerDeath(EntityDeathEvent event) {
        if(!event.getEntityType().equals(EntityType.ILLUSIONER))
            return;

        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 50, 0.8f);
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 50, 1f);
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_WITHER_DEATH, 50, 2f);

        ExperienceOrb eo1 = (ExperienceOrb) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
        eo1.setExperience(NumberUtils.randInt(20000,25000));
    }
}
