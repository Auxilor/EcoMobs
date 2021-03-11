package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.util.OptionedSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttackListeners implements Listener {
    /**
     * Called when a player attacks an illusioner.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onIllusionerAttack(@NotNull final EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.ILLUSIONER)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        handleAttack((LivingEntity) event.getDamager(), (Player) event.getEntity());
    }

    /**
     * Called when a player attacks an illusioner.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAttackIllusioner(@NotNull final EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ILLUSIONER)) {
            return;
        }

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

        handleAttack((LivingEntity) event.getEntity(), player);
    }

    private void handleAttack(@NotNull final LivingEntity illusioner,
                              @NotNull final Player player) {
        OptionedSound hitSound = IllusionerManager.OPTIONS.getGameplayOptions().getHitSound();
        if (hitSound.isBroadcast()) {
            player.getWorld().playSound(illusioner.getLocation(), hitSound.getSound(), hitSound.getVolume(), hitSound.getPitch());
        } else {
            player.playSound(illusioner.getLocation(), hitSound.getSound(), hitSound.getVolume(), hitSound.getPitch());
        }

        IllusionerManager.OPTIONS.getGameplayOptions().getEffectOptions().forEach(effectOption -> {
            if (NumberUtils.randFloat(0, 100) > effectOption.getChance()) {
                return;
            }

            player.addPotionEffect(new PotionEffect(effectOption.getEffectType(), effectOption.getDuration(), effectOption.getLevel() - 1));
        });

        if (IllusionerManager.OPTIONS.getGameplayOptions().isShuffle()) {
            if (NumberUtils.randFloat(0, 100) < IllusionerManager.OPTIONS.getGameplayOptions().getShuffleChance()) {
                List<ItemStack> hotbar = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    hotbar.add(player.getInventory().getItem(i));
                }
                Collections.shuffle(hotbar);
                int i2 = 0;
                for (ItemStack item : hotbar) {
                    player.getInventory().setItem(i2, item);
                    i2++;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1, 0.5f);
            }
        }

        IllusionerManager.OPTIONS.getGameplayOptions().getSummonerOptions().forEach(summonerOption -> {
            if (NumberUtils.randFloat(0, 100) > summonerOption.getChance()) {
                return;
            }

            Location loc = player.getLocation().add(NumberUtils.randInt(2, 6), 0, NumberUtils.randInt(2, 6));
            while (!loc.getBlock().getType().equals(Material.AIR)) {
                loc.add(0, 1, 0);
            }
            player.getWorld().spawnEntity(loc, summonerOption.getType());

            OptionedSound summonSound = IllusionerManager.OPTIONS.getGameplayOptions().getSummonSound();
            if (summonSound.isBroadcast()) {
                player.getWorld().playSound(illusioner.getLocation(), summonSound.getSound(), summonSound.getVolume(), summonSound.getPitch());
            } else {
                player.playSound(illusioner.getLocation(), summonSound.getSound(), summonSound.getVolume(), summonSound.getPitch());
            }
        });
    }

    /**
     * Called when the illusioner is damaged.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onIllusionerDamage(@NotNull final EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ILLUSIONER)) {
            return;
        }

        if (IllusionerManager.OPTIONS.getGameplayOptions().isIgnoreExplosionDamage()) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                event.setCancelled(true);
            }
        }

        if (IllusionerManager.OPTIONS.getGameplayOptions().isIgnoreFire()) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                event.setCancelled(true);
            }
        }

        if (IllusionerManager.OPTIONS.getGameplayOptions().isIgnoreSuffocation()) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
                event.setCancelled(true);
            }
        }

        if (IllusionerManager.OPTIONS.getGameplayOptions().isTeleport()) {
            if (NumberUtils.randFloat(0, 100) < IllusionerManager.OPTIONS.getGameplayOptions().getTeleportChance()) {
                int range = IllusionerManager.OPTIONS.getGameplayOptions().getTeleportRange();
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
                OptionedSound optionedSound = IllusionerManager.OPTIONS.getGameplayOptions().getTeleportSound();
                location.getWorld().playSound(location, optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
            }
        }
    }
}
