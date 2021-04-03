package com.willfp.ecobosses.bosses.util.bosstype;

import com.willfp.ecobosses.proxy.util.CustomEntities;
import com.willfp.ecobosses.proxy.util.CustomEntity;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unchecked")
public class BossEntityUtils {
    /**
     * Get boss type.
     *
     * @param id The name.
     * @return The boss type.
     */
    public static BossType getBossType(@NotNull final String id) {
        try {
            Class<? extends LivingEntity> type = (Class<? extends LivingEntity>) EntityType.valueOf(id.toUpperCase()).getEntityClass();
            assert type != null;
            return new VanillaBossType(type);
        } catch (IllegalArgumentException e) {
            Class<? extends CustomEntity<? extends LivingEntity>> proxy = CustomEntities.getEntityClass(id.toLowerCase());
            if (proxy != null) {
                return new CustomBossType(proxy);
            }
        }

        return null;
    }
}
