package com.willfp.ecobosses.bosses.util;

import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.LivingEcoBoss;
import com.willfp.ecobosses.bosses.util.obj.DamagerProperty;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@UtilityClass
@SuppressWarnings("unchecked")
public class BossUtils {
    /**
     * Instance of EcoBosses.
     */
    private static final EcoBossesPlugin PLUGIN = EcoBossesPlugin.getInstance();

    /**
     * Get {@link EcoBoss} from an entity.
     *
     * @param entity The entity.
     * @return The boss, or null if not a boss.
     */
    @Nullable
    public EcoBoss getBoss(@NotNull final LivingEntity entity) {
        if (!entity.getPersistentDataContainer().has(PLUGIN.getNamespacedKeyFactory().create("boss"), PersistentDataType.STRING)) {
            return null;
        }

        String bossName = entity.getPersistentDataContainer().get(PLUGIN.getNamespacedKeyFactory().create("boss"), PersistentDataType.STRING);

        if (bossName == null) {
            return null;
        }

        return EcoBosses.getByName(bossName);
    }

    /**
     * Get top damagers for a boss.
     *
     * @param entity The boss entity.
     * @return A list of the top damagers, sorted.
     */
    public List<DamagerProperty> getTopDamagers(@NotNull final LivingEntity entity) {
        if (getBoss(entity) == null) {
            return new ArrayList<>();
        }

        List<DamagerProperty> topDamagers;
        if (entity.hasMetadata("ecobosses-top-damagers")) {
            topDamagers = (List<DamagerProperty>) entity.getMetadata("ecobosses-top-damagers").get(0).value();
        } else {
            topDamagers = new ArrayList<>();
        }
        assert topDamagers != null;

        topDamagers.sort(Comparator.comparingDouble(DamagerProperty::damage));
        Collections.reverse(topDamagers);

        return topDamagers;
    }

    /**
     * Kill all bosses.
     *
     * @return The amount of bosses killed.
     */
    public int killAllBosses() {
        return killAllBosses(false);
    }

    /**
     * Kill all bosses.
     *
     * @param force If all entities should be checked for being bosses.
     * @return The amount of bosses killed.
     */
    public int killAllBosses(final boolean force) {
        int amount = 0;
        for (EcoBoss boss : EcoBosses.values()) {
            for (UUID uuid : boss.getLivingBosses().keySet()) {
                LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                assert entity != null;
                entity.damage(10000000);
                amount++;
            }
        }

        if (force) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (!(entity instanceof LivingEntity)) {
                        continue;
                    }

                    if (BossUtils.getBoss((LivingEntity) entity) == null) {
                        continue;
                    }

                    entity.remove();
                }
            }
        }

        List<KeyedBossBar> bars = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining(bars::add);
        for (KeyedBossBar bar : bars) {
            if (bar.getKey().toString().startsWith("ecobosses:boss")) {
                BossBar bossBar = Bukkit.getBossBar(bar.getKey());
                assert bossBar != null;
                bossBar.removeAll();
                bossBar.setVisible(false);
                Bukkit.removeBossBar(bar.getKey());
            }
        }

        return amount;
    }

    /**
     * Get player from entity if player or projectile.
     *
     * @param entity The entity.
     * @return The player, or null if not a player.
     */
    @Nullable
    public Player getPlayerFromEntity(@NotNull final Entity entity) {
        Player player = null;

        if (entity instanceof Player) {
            player = (Player) entity;
        } else if (entity instanceof Projectile) {
            if (((Projectile) entity).getShooter() instanceof Player) {
                player = (Player) ((Projectile) entity).getShooter();
            }
        }

        return player;
    }

    /**
     * Warn if a boss is null.
     *
     * @param boss The boss.
     */
    public void warnIfNull(@Nullable final LivingEcoBoss boss) {
        if (boss != null) {
            return;
        }

        PLUGIN.getLogger().severe("Boss is null! Report this to Auxilor (https://discord.gg/ZcwpSsE)");
        PLUGIN.getLogger().severe("Send this stack-trace in the relevant channel.");
        throw new NullPointerException("Boss is null!");
    }

    /**
     * Warn if an entity is null.
     */
    public void warnIfNull(@Nullable final Entity entity) {
        if (entity != null) {
            return;
        }

        PLUGIN.getLogger().severe("Entity is null! Report this to Auxilor (https://discord.gg/ZcwpSsE)");
        PLUGIN.getLogger().severe("Send this stack-trace in the relevant channel.");
        throw new NullPointerException("Entity is null!");
    }
}
