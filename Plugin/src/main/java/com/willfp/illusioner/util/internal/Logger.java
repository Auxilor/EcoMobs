package com.willfp.illusioner.util.internal;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.util.StringUtils;

/**
 * The internal logger for Illusioner
 * Automatically formats all inputs using {@link StringUtils#translate(String)}
 */
public class Logger {
    private static final IllusionerPlugin INSTANCE = IllusionerPlugin.getInstance();

    /**
     * Print an info (neutral) message to console
     *
     * @param message The message to send
     */
    public static void info(String message) {
        INSTANCE.getLogger().info(StringUtils.translate(message));
    }

    /**
     * Print a warning to console
     *
     * @param message The warning
     */
    public static void warn(String message) {
        INSTANCE.getLogger().warning(StringUtils.translate(message));
    }

    /**
     * Print an error to console
     *
     * @param message The error
     */
    public static void error(String message) {
        INSTANCE.getLogger().severe(StringUtils.translate(message));
    }
}
