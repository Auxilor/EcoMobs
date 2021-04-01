package com.willfp.ecobosses.bosses;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.bukkit.scheduling.EcoBukkitRunnable;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import com.willfp.ecobosses.bosses.tick.tickers.BossBarTicker;
import com.willfp.ecobosses.bosses.tick.tickers.HealthPlaceholderTicker;
import com.willfp.ecobosses.bosses.tick.tickers.TargetTicker;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import com.willfp.ecobosses.bosses.util.obj.attacks.EffectOption;
import com.willfp.ecobosses.bosses.util.obj.attacks.SummonsOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class LivingEcoBoss extends PluginDependent {
    /**
     * The entity.
     */
    private final LivingEntity entity;

    /**
     * The boss.
     */
    private final EcoBoss boss;

    /**
     * The boss tickers.
     */
    private final Set<BossTicker> tickers;

    /**
     * Create new living EcoBoss.
     *
     * @param plugin Instance of EcoBosses.
     * @param entity The entity.
     * @param boss   The boss.
     */
    public LivingEcoBoss(@NotNull final AbstractEcoPlugin plugin,
                         @NotNull final LivingEntity entity,
                         @NotNull final EcoBoss boss) {
        super(plugin);
        this.entity = entity;
        this.boss = boss;

        this.onSpawn();

        // Tickers
        this.tickers = new HashSet<>();
        this.tickers.add(new HealthPlaceholderTicker());
        this.tickers.add(new TargetTicker());
        if (boss.isBossbarEnabled()) {
            this.tickers.add(
                    new BossBarTicker(
                            Bukkit.getServer().createBossBar(
                                    entity.getCustomName(),
                                    boss.getBossbarProperties().getColor(),
                                    boss.getBossbarProperties().getStyle(),
                                    (BarFlag) null
                            ),
                            this.getPlugin().getConfigYml().getInt("bossbar-radius")
                    )
            );
        }
        this.tickers.addAll(boss.createEffectTickers());


        AtomicLong currentTick = new AtomicLong(0);
        this.getPlugin().getRunnableFactory().create(runnable -> {
            this.tick(currentTick.getAndAdd(1), runnable);
        }).runTaskTimer(0, 1);
    }

    private void onSpawn() {
        entity.getPersistentDataContainer().set(this.getPlugin().getNamespacedKeyFactory().create("boss"), PersistentDataType.STRING, boss.getName());
        entity.setPersistent(true);

        entity.setCustomName(boss.getDisplayName());
        entity.setCustomNameVisible(true);

        AttributeInstance movementSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        assert movementSpeed != null;
        movementSpeed.addModifier(new AttributeModifier(entity.getUniqueId(), "ecobosses-movement-multiplier", boss.getMovementSpeedMultiplier() - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;
        maxHealth.setBaseValue(boss.getMaxHealth());

        entity.setHealth(maxHealth.getValue());

        AttributeInstance attackDamage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        assert attackDamage != null;
        attackDamage.setBaseValue(boss.getAttackDamage());

        for (OptionedSound sound : boss.getSpawnSounds()) {
            entity.getWorld().playSound(entity.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }

        for (String spawnMessage : boss.getSpawnMessages()) {
            Bukkit.broadcastMessage(spawnMessage
                    .replace("%x%", StringUtils.internalToString(entity.getLocation().getBlockX()))
                    .replace("%y%", StringUtils.internalToString(entity.getLocation().getBlockY()))
                    .replace("%z%", StringUtils.internalToString(entity.getLocation().getBlockZ()))
            );
        }
    }

    private void tick(final long tick,
                      @NotNull final EcoBukkitRunnable runnable) {
        for (BossTicker ticker : tickers) {
            ticker.tick(boss, entity, tick);
        }
        if (entity.isDead()) {
            for (BossTicker ticker : tickers) {
                ticker.onDeath(boss, entity, tick);
            }
            runnable.cancel();
            boss.removeLivingBoss(entity.getUniqueId());
        }
    }

    /**
     * Handle an attack to the player.
     *
     * @param player The player.
     */
    public void handleAttack(@NotNull final Player player) {
        for (OptionedSound sound : boss.getInjureSounds()) {
            player.getWorld().playSound(entity.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }

        for (EffectOption effect : boss.getEffects()) {
            if (NumberUtils.randFloat(0, 100) > effect.getChance()) {
                continue;
            }

            player.addPotionEffect(new PotionEffect(effect.getEffectType(), effect.getDuration(), effect.getLevel()));
        }

        if (NumberUtils.randFloat(0, 100) < boss.getShuffleChance()) {
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

        for (SummonsOption summon : boss.getSummons()) {
            if (NumberUtils.randFloat(0, 100) > summon.getChance()) {
                continue;
            }

            Location loc = player.getLocation().add(NumberUtils.randInt(2, 6), 0, NumberUtils.randInt(2, 6));
            while (!loc.getBlock().getType().equals(Material.AIR)) {
                loc.add(0, 1, 0);
            }
            Entity summonedEntity = player.getWorld().spawnEntity(loc, summon.getType());
            if (summonedEntity instanceof Mob) {
                ((Mob) summonedEntity).setTarget(player);
            }

            for (OptionedSound sound : boss.getSummonSounds()) {
                player.getWorld().playSound(entity.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
            }
        }
    }
}
