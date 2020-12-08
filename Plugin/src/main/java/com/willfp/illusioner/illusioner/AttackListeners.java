package com.willfp.illusioner.illusioner;

import com.willfp.illusioner.util.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttackListeners implements Listener {
    @EventHandler
    public void onIllusionerAttack(EntityDamageByEntityEvent event) {
        if(!event.getDamager().getType().equals(EntityType.ILLUSIONER))
            return;

        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1f);
        if(!player.hasPotionEffect(PotionEffectType.CONFUSION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 10));
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 2f);
        }
        if(NumberUtils.randInt(1, 10) <= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 2f);
        }
        if(NumberUtils.randInt(1, 10) <= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 2));
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 2f);
        }

        if(NumberUtils.randInt(1, 20) == 1) {
            List<ItemStack> hotbar = new ArrayList<>();
            for(int i = 0; i<9; i++) {
                hotbar.add(player.getInventory().getItem(i));
            }
            Collections.shuffle(hotbar);
            int i2 = 0;
            for(ItemStack item : hotbar) {
                player.getInventory().setItem(i2, item);
                i2++;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1, 0.5f);
        }
    }

    @EventHandler
    public void onIllusionerDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!event.getEntity().getType().equals(EntityType.ILLUSIONER))
            return;

        if(!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();

        if(NumberUtils.randInt(1, 10) == 1) {
            Location loc = player.getLocation().add(NumberUtils.randInt(2,6), 0, NumberUtils.randInt(2,6));
            while(!loc.getBlock().getType().equals(Material.AIR)) {
                loc.add(0, 1, 0);
            }
            player.getWorld().spawnEntity(loc, EntityType.EVOKER);
            player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1, 2f);
        }

        if(NumberUtils.randInt(1, 10) == 1) {
            Location loc = player.getLocation().add(NumberUtils.randInt(2,6), 0, NumberUtils.randInt(2,6));
            while(!loc.getBlock().getType().equals(Material.AIR)) {
                loc.add(0, 1, 0);
            }
            player.getWorld().spawnEntity(loc, EntityType.VINDICATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1, 2f);
        }

        ExperienceOrb experienceOrb = (ExperienceOrb) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(NumberUtils.randInt(5,20));
    }

    @EventHandler
    public void onIllusionerDamage(EntityDamageEvent event) {
        if(!event.getEntity().getType().equals(EntityType.ILLUSIONER))
            return;

        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            event.setCancelled(true);
        }
    }
}
