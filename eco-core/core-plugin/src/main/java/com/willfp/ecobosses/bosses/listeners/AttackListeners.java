package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.bosses.util.obj.DamagerProperty;
import com.willfp.ecobosses.bosses.util.obj.ImmunityOptions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AttackListeners extends PluginDependent implements Listener {
    /**
     * Create new attack listeners.
     *
     * @param plugin Instance of EcoBosses.
     */
    public AttackListeners(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Called when a player attacks a boss.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAttackBoss(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        Player player = null;

        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                player = (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }

        if (player == null) {
            return;
        }

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        if (boss.isAttackOnInjure()) {
            boss.handleAttack(entity, player);
        }
    }

    /**
     * Track top damage players.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void topDamageTracker(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        Player temp = null;

        if (event.getDamager() instanceof Player) {
            temp = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                temp = (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }

        if (temp == null) {
            return;
        }

        Player player = temp;

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        List<DamagerProperty> topDamagers = BossUtils.getTopDamagers(entity);

        double playerDamage;

        Optional<DamagerProperty> damager = topDamagers.stream().filter(damagerProperty -> damagerProperty.getPlayer().equals(player)).findFirst();
        playerDamage = damager.map(DamagerProperty::getDamage).orElse(0.0);

        playerDamage += event.getFinalDamage();
        topDamagers.removeIf(damagerProperty -> damagerProperty.getPlayer().equals(player));
        topDamagers.add(new DamagerProperty(player, playerDamage));

        entity.removeMetadata("ecobosses-top-damagers", this.getPlugin());
        entity.setMetadata("ecobosses-top-damagers", this.getPlugin().getMetadataValueFactory().create(topDamagers));
    }

    /**
     * Called when a boss attacks a player.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAttackPlayer(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getDamager();

        Player player = (Player) event.getEntity();

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        boss.handleAttack(entity, player);
    }

    /**
     * Called when a boss is damaged.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void defenceListener(@NotNull final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        ImmunityOptions immunities = boss.getImmunityOptions();

        if (immunities.isImmuneToFire() && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
            event.setCancelled(true);
        }

        if (immunities.isImmuneToSuffocation() && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            event.setCancelled(true);
        }

        if (immunities.isImmuneToDrowning() && event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
        }

        if (immunities.isImmuneToExplosions() && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            event.setCancelled(true);
        }

        if (immunities.isImmuneToProjectiles() && (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
            event.setCancelled(true);
        }

        if (boss.isTeleportationEnabled()) {
            if (NumberUtils.randFloat(0, 100) < boss.getTeleportOptions().getChance()) {
                int range = boss.getTeleportOptions().getRange();
                List<Location> valid = new ArrayList<>();
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        for (int z = -range; z <= range; z++) {
                            Location location = event.getEntity().getLocation().clone();
                            location.setX(location.getX() + x);
                            location.setY(location.getY() + y);
                            location.setZ(location.getZ() + z);

                            Block block = location.getBlock();

                            if (block.getType() == Material.AIR
                                    && block.getRelative(BlockFace.UP).getType() == Material.AIR
                                    && !(block.getRelative(BlockFace.DOWN).isLiquid() || block.getRelative(BlockFace.DOWN).getType() == Material.AIR)) {
                                valid.add(location);
                            }
                        }
                    }
                }

                if (valid.isEmpty()) {
                    return;
                }

                Collections.shuffle(valid);
                Location location = valid.get(0);

                event.getEntity().teleport(location);
            }
        }
    }
}
