package com.willfp.ecobosses.bosses.util.bosstype;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class VanillaBossType extends BossType {
    /**
     * The entity type.
     */
    private final Class<? extends LivingEntity> entityClass;

    /**
     * Create new vanilla boss type.
     *
     * @param entityClass The entity class.
     */
    VanillaBossType(@NotNull final Class<? extends LivingEntity> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public LivingEntity spawnBossEntity(@NotNull final Location location) {
        LivingEntity result = Objects.requireNonNull(location.getWorld()).spawn(location, entityClass);
        return result;
    }
}
