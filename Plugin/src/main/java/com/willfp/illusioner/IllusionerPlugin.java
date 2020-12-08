package com.willfp.illusioner;

import com.willfp.illusioner.util.internal.Loader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Main class for Illusioner
 */
public class IllusionerPlugin extends JavaPlugin {
    /**
     * Instance of Illusioner
     */
    private static IllusionerPlugin instance;

    /**
     * NMS version
     */
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    /**
     * Calls {@link Loader#load()}
     */
    public void onEnable() {
        Loader.load();
    }

    /**
     * Calls {@link Loader#unload()}
     */
    public void onDisable() {
        Loader.unload();
    }

    /**
     * Sets instance
     */
    public void onLoad() {
        instance = this;
    }

    /**
     * Get plugin instance
     * @return Plugin instance
     */
    public static IllusionerPlugin getInstance() {
        return instance;
    }
}
