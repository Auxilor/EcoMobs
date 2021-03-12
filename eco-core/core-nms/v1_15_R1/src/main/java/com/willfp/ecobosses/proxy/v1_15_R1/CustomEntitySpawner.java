package com.willfp.ecobosses.proxy.v1_15_R1;

import com.willfp.ecobosses.proxy.proxies.CustomEntitySpawnerProxy;
import com.willfp.ecobosses.proxy.proxies.CustomIllusionerProxy;
import com.willfp.ecobosses.proxy.util.CustomEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CustomEntitySpawner implements CustomEntitySpawnerProxy {
    @Override
    public <T extends LivingEntity> @Nullable T spawnCustomEntity(final Class<? extends CustomEntity<? extends LivingEntity>> entityClass,
                                                                  @NotNull final Location location) {
        if (entityClass.equals(CustomIllusionerProxy.class)) {
            return (T) CustomIllusioner.spawn(location);
        }

        return null;
    }
}
