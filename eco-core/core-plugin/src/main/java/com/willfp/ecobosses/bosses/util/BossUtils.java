package com.willfp.ecobosses.bosses.util;

import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
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
}
