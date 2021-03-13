package com.willfp.ecobosses.bosses.listeners;


import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.events.entitydeathbyentity.EntityDeathByEntityEvent;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.bosses.util.obj.DamagerProperty;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeathListeners extends PluginDependent implements Listener {
    /**
     * Create new death listeners.
     *
     * @param plugin Instance of EcoBosses.
     */
    public DeathListeners(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

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

        List<DamagerProperty> topDamagers = BossUtils.getTopDamagers(entity);

        DamagerProperty top = null;
        DamagerProperty second = null;
        DamagerProperty third = null;

        if (topDamagers.size() >= 1) {
            top = topDamagers.get(0);
        }
        if (topDamagers.size() >= 2) {
            second = topDamagers.get(1);
        }
        if (topDamagers.size() >= 3) {
            third = topDamagers.get(2);
        }

        String na = this.getPlugin().getLangYml().getString("na");

        String topDamager = top == null ? na : top.getPlayer().getDisplayName();
        String topDamage = top == null ? na : StringUtils.internalToString(top.getDamage());

        String secondDamager = second == null ? na : second.getPlayer().getDisplayName();
        String secondDamage = second == null ? na : StringUtils.internalToString(second.getDamage());

        String thirdDamager = third == null ? na : third.getPlayer().getDisplayName();
        String thirdDamage = third == null ? na : StringUtils.internalToString(third.getDamage());

        for (String spawnMessage : boss.getDeathMessages()) {
            Bukkit.broadcastMessage(spawnMessage
                    .replace("%top%", topDamager)
                    .replace("%top_damage%", topDamage)
                    .replace("%second%", secondDamager)
                    .replace("%second_damage%", secondDamage)
                    .replace("%third%", thirdDamager)
                    .replace("%third_damage%", thirdDamage)
            );
        }

        List<ItemStack> drops = new ArrayList<>();
        boss.getDrops().forEach((aDouble, itemStack) -> {
            if (NumberUtils.randFloat(0, 100) < aDouble) {
                drops.add(itemStack);
            }
        });

        event.getDrops().addAll(drops);
        event.setDroppedExp(boss.getExperienceOptions().generateXp());
    }
}
