package com.willfp.ecobosses.bosses.util;

import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.util.obj.DamagerProperty;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.LivingEntity;
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

        topDamagers.sort(Comparator.comparingDouble(DamagerProperty::getDamage));
        Collections.reverse(topDamagers);

        return topDamagers;
    }

    /**
     * Kill all bosses.
     *
     * @return The amount of bosses killed.
     */
    public int killAllBosses() {
        int amount = 0;
        for (EcoBoss boss : EcoBosses.values()) {
            for (UUID uuid : boss.getLivingBosses().keySet()) {
                LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                assert entity != null;
                entity.damage(10000000);
                amount++;
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
}
