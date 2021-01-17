package com.willfp.illusioner.util;

import com.willfp.eco.util.proxy.AbstractProxy;
import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.proxy.util.ProxyFactory;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ProxyUtils {
    /**
     * Get the implementation of a specified proxy.
     *
     * @param proxyClass The proxy interface.
     * @param <T>        The type of the proxy.
     * @return The proxy implementation.
     */
    public @NotNull <T extends AbstractProxy> T getProxy(@NotNull final Class<T> proxyClass) {
        return new ProxyFactory<>(IllusionerPlugin.getInstance(), proxyClass).getProxy();
    }
}
