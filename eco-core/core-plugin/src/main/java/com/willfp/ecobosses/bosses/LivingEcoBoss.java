package com.willfp.ecobosses.bosses;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.scheduling.RunnableTask;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.effects.Effect;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import com.willfp.ecobosses.bosses.tick.tickers.BossBarTicker;
import com.willfp.ecobosses.bosses.tick.tickers.ChunkLoadTicker;
import com.willfp.ecobosses.bosses.tick.tickers.HealthPlaceholderTicker;
import com.willfp.ecobosses.bosses.tick.tickers.TargetTicker;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LivingEcoBoss extends PluginDependent<EcoPlugin> {
    /**
     * The entity.
     */
    @Getter
    private LivingEntity entity;

    /**
     * The entity UUID.
     */
    private final UUID uuid;

    /**
     * The boss.
     */
    private final EcoBoss boss;

    /**
     * The boss tickers.
     */
    private final List<BossTicker> tickers;

    /**
     * The effects.
     */
    private final List<Effect> effects;

    /**
     * Create new living EcoBoss.
     *
     * @param plugin Instance of EcoBosses.
     * @param entity The entity.
     * @param boss   The boss.
     */
    public LivingEcoBoss(@NotNull final EcoPlugin plugin,
                         @NotNull final LivingEntity entity,
                         @NotNull final EcoBoss boss) {
        super(plugin);
        this.entity = entity;
        this.uuid = entity.getUniqueId();
        this.boss = boss;

        this.onSpawn();

        // Tickers
        this.tickers = new ArrayList<>();
        this.tickers.add(new HealthPlaceholderTicker());
        this.tickers.add(new TargetTicker(boss.getTargetMode(), boss.getTargetDistance()));
        this.tickers.add(new ChunkLoadTicker());
        if (boss.isBossbarEnabled()) {
            this.tickers.add(
                    new BossBarTicker(
                            Bukkit.getServer().createBossBar(
                                    plugin.getNamespacedKeyFactory().create("boss_" + NumberUtils.randInt(0, 1000000)),
                                    entity.getCustomName(),
                                    boss.getBossbarProperties().color(),
                                    boss.getBossbarProperties().style(),
                                    (BarFlag) null
                            ),
                            this.getPlugin().getConfigYml().getInt("bossbar-radius")
                    )
            );
        }

        // Effects
        this.effects = new ArrayList<>();
        this.effects.addAll(boss.createEffects());

        AtomicLong currentTick = new AtomicLong(0);
        this.getPlugin().getRunnableFactory().create(runnable -> this.tick(currentTick.getAndAdd(1), runnable)).runTaskTimer(0, 1);
    }

    private void onSpawn() {
        entity.getPersistentDataContainer().set(this.getPlugin().getNamespacedKeyFactory().create("boss"), PersistentDataType.STRING, boss.getName());
        entity.setPersistent(true);
        entity.setRemoveWhenFarAway(false);

        entity.setCustomName(boss.getDisplayName());
        entity.setCustomNameVisible(true);

        AttributeInstance movementSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        assert movementSpeed != null;
        movementSpeed.addModifier(new AttributeModifier(entity.getUniqueId(), "ecobosses-movement-multiplier", boss.getMovementSpeedMultiplier() - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;
        maxHealth.setBaseValue(boss.getMaxHealth());

        entity.setHealth(maxHealth.getValue());

        AttributeInstance followRange = entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        assert followRange != null;
        followRange.setBaseValue(boss.getFollowRange());

        AttributeInstance attackDamage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        assert attackDamage != null;
        attackDamage.setBaseValue(boss.getAttackDamage());

        for (OptionedSound sound : boss.getSpawnSounds()) {
            entity.getWorld().playSound(entity.getLocation(), sound.sound(), sound.volume(), sound.pitch());
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
                      @NotNull final RunnableTask runnable) {
        this.entity = (LivingEntity) Bukkit.getEntity(uuid);

        if (entity == null || entity.isDead() || boss.getLivingBoss(entity) == null) {
            for (BossTicker ticker : tickers) {
                ticker.onDeath(boss, entity, tick);
            }
            for (Effect effect : effects) {
                effect.onDeath(boss, entity, tick);
            }
            boss.removeLivingBoss(uuid);
            runnable.cancel();
            return;
        }

        for (BossTicker ticker : tickers) {
            ticker.tick(boss, entity, tick);
        }
        for (Effect effect : effects) {
            effect.tick(boss, entity, tick);
        }
    }

    /**
     * Handle an attack to the player.
     *
     * @param player The player.
     */
    public void handleAttack(@NotNull final Player player) {
        for (OptionedSound sound : boss.getInjureSounds()) {
            player.getWorld().playSound(entity.getLocation(), sound.sound(), sound.volume(), sound.pitch());
        }

        for (Effect effect : effects) {
            effect.onAttack(boss, entity, player);
        }
    }
}
