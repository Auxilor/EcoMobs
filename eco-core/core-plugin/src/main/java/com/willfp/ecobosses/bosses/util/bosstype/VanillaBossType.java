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
     * If the entity is Creeper and should or should not be charged.
     */
    private final boolean creeperCharged;

    /**
     * Create new vanilla boss type.
     *
     * @param entityClass The entity class.
     */
    VanillaBossType(@NotNull final Class<? extends LivingEntity> entityClass) {
        this.entityClass = entityClass;
        this.creeperCharged = false;
    }

    /**
     * Create new vanilla boss type.
     *
     * @param entityClass The entity class.
     * @param creeperCharged The creeper power state.
     */
    VanillaBossType(@NotNull final Class<? extends LivingEntity> entityClass, boolean creeperCharged) {
        this.entityClass = entityClass;
        this.creeperCharged = creeperCharged;
    }

    @Override
    public LivingEntity spawnBossEntity(@NotNull final Location location) {
        LivingEntity result = Objects.requireNonNull(location.getWorld()).spawn(location, entityClass);
        if (result instanceof Creeper creeper && creeperCharged) creeper.setPowered(true);
        return result;
    }
}
