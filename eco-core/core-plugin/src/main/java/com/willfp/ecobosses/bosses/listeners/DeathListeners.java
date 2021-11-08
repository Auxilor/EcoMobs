package com.willfp.ecobosses.bosses.listeners;


import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.EntityDeathByEntityEvent;
import com.willfp.eco.core.tuples.Pair;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.bosses.util.obj.DamagerProperty;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeathListeners extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Create new death listeners.
     *
     * @param plugin Instance of EcoBosses.
     */
    public DeathListeners(@NotNull final EcoPlugin plugin) {
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
            entity.getWorld().playSound(entity.getLocation(), sound.sound(), sound.volume(), sound.pitch());
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

        String topDamager = top == null ? na : Objects.requireNonNull(Bukkit.getPlayer(top.playerUUID()).getDisplayName(), na);
        String topDamage = top == null ? na : StringUtils.internalToString(top.damage());

        String secondDamager = second == null ? na : Objects.requireNonNull(Bukkit.getPlayer(second.playerUUID()).getDisplayName(), na);
        String secondDamage = second == null ? na : StringUtils.internalToString(second.damage());

        String thirdDamager = third == null ? na : Objects.requireNonNull(Bukkit.getPlayer(third.playerUUID()).getDisplayName(), na);
        String thirdDamage = third == null ? na : StringUtils.internalToString(third.damage());

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

        for (int i = 1; i <= 3; i++) {
            List<Pair<Double, String>> topDamagerCommands = boss.getTopDamagerCommands().get(i);
            for (Pair<Double, String> pair : topDamagerCommands) {
                if (top != null && i == 1) {
                    if (NumberUtils.randFloat(0, 100) < pair.getFirst()) {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), pair.getSecond().replace("%player%", Bukkit.getOfflinePlayer(top.playerUUID()).getName()));
                    }
                }
                if (second != null && i == 2) {
                    if (NumberUtils.randFloat(0, 100) < pair.getFirst()) {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), pair.getSecond().replace("%player%", Bukkit.getOfflinePlayer(second.playerUUID()).getName()));
                    }
                }
                if (third != null && i == 3) {
                    if (NumberUtils.randFloat(0, 100) < pair.getFirst()) {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), pair.getSecond().replace("%player%", Bukkit.getOfflinePlayer(third.playerUUID()).getName()));
                    }
                }
            }
        }

        List<ItemStack> drops = boss.generateDrops();

        for (Entity nearby : entity.getNearbyEntities(boss.getNearbyRadius(), boss.getNearbyRadius(), boss.getNearbyRadius())) {
            if (nearby instanceof Player) {
                String playerName = nearby.getName();
                for (Map.Entry<String, Double> entry : boss.getNearbyPlayersCommands().entrySet()) {
                    if (NumberUtils.randFloat(0, 100) < entry.getValue()) {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), entry.getKey().replace("%player%", playerName));
                    }
                }
            }
        }

        event.getDrops().addAll(drops);
        event.setDroppedExp(boss.getExperienceOptions().generateXp());
    }

    @EventHandler
    public void preventSplit(@NotNull final SlimeSplitEvent event) {
        LivingEntity entity = event.getEntity();

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        event.setCancelled(true);
    }
}
