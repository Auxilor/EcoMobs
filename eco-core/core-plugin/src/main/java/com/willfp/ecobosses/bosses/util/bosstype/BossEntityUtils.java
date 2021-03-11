package com.willfp.ecobosses.bosses.util.bosstype;

import com.willfp.ecobosses.proxy.util.CustomEntities;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class BossEntityUtils {
    /**
     * Get boss type.
     *
     * @param id The name.
     * @return The boss type.
     */
    public static BossType getBossType(@NotNull final String id) {
        if (CustomEntities.getEntityClass(id) != null) {
            return new CustomBossType(Objects.requireNonNull(CustomEntities.getEntityClass(id)));
        } else {
            return new VanillaBossType(Objects.requireNonNull(EntityType.valueOf(id.toUpperCase()).getEntityClass()));
        }
    }
}
