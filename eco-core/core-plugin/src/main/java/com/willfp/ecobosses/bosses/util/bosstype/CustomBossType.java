package com.willfp.ecobosses.bosses.util.bosstype;

import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.proxy.proxies.CustomEntitySpawnerProxy;
import com.willfp.ecobosses.proxy.util.CustomEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

class CustomBossType extends BossType {
    /**
     * The entity type.
     */
    private final Class<? extends CustomEntity<? extends LivingEntity>> entityClass;

    /**
     * Create new vanilla boss type.
     *
     * @param entityClass The entity class.
     */
    CustomBossType(@NotNull final Class<? extends CustomEntity<? extends LivingEntity>> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public LivingEntity spawnBossEntity(@NotNull final Location location) {
        return EcoBossesPlugin.getInstance().getProxy(CustomEntitySpawnerProxy.class).spawnCustomEntity(entityClass, location);
    }
}
