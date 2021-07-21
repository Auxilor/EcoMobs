package com.willfp.ecobosses.proxy.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.willfp.ecobosses.proxy.proxies.CustomIllusionerProxy;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class CustomEntities {
    /**
     * Registered custom entities.
     */
    private static final BiMap<String, Class<? extends CustomEntity<? extends LivingEntity>>> REGISTRY = new ImmutableBiMap.Builder<String, Class<? extends CustomEntity<? extends LivingEntity>>>()
            .put("custom_illusioner", CustomIllusionerProxy.class)
            .build();

    /**
     * Get entity class.
     *
     * @param id The entity id.
     * @return The class.
     */
    @Nullable
    public Class<? extends CustomEntity<? extends LivingEntity>> getEntityClass(@NotNull final String id) {
        return REGISTRY.get(id);
    }
}
