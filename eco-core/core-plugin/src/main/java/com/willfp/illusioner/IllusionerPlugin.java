package com.willfp.illusioner;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.display.DisplayModule;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.illusioner.commands.CommandIldrop;
import com.willfp.illusioner.commands.CommandIlreload;
import com.willfp.illusioner.config.IllusionerConfigs;
import com.willfp.illusioner.illusioner.IllusionerManager;
import com.willfp.illusioner.illusioner.listeners.AttackListeners;
import com.willfp.illusioner.illusioner.listeners.DeathListeners;
import com.willfp.illusioner.illusioner.listeners.SpawnListeners;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class IllusionerPlugin extends AbstractEcoPlugin {
    /**
     * Instance of the plugin.
     */
    @Getter
    private static IllusionerPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public IllusionerPlugin() {
        super("Illusioner", 86576, 9596, "com.willfp.illusioner.proxy", "&9");
        instance = this;
    }

    /**
     * Code executed on plugin enable.
     */
    @Override
    public void enable() {

    }

    /**
     * Code executed on plugin disable.
     */
    @Override
    public void disable() {

    }

    /**
     * Nothing is called on plugin load.
     */
    @Override
    public void load() {
        // Nothing needs to be called on load
    }

    /**
     * Code executed on reload.
     */
    @Override
    public void onReload() {
        IllusionerManager.OPTIONS.reload();
    }

    /**
     * Code executed after server is up.
     */
    @Override
    public void postLoad() {
        // Nothing is executed post-load.
    }

    /**
     * EcoEnchants-specific integrations.
     *
     * @return A list of all integrations.
     */
    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return new ArrayList<>();
    }

    /**
     * EcoEnchants-specific commands.
     *
     * @return A list of all commands.
     */
    @Override
    public List<AbstractCommand> getCommands() {
        return Arrays.asList(
                new CommandIlreload(this),
                new CommandIldrop(this)
        );
    }

    /**
     * Packet Adapters for enchant display.
     *
     * @return A list of packet adapters.
     */
    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
        return new ArrayList<>();
    }

    /**
     * EcoEnchants-specific listeners.
     *
     * @return A list of all listeners.
     */
    @Override
    public List<Listener> getListeners() {
        return Arrays.asList(
                new AttackListeners(),
                new DeathListeners(),
                new SpawnListeners(this)
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return Arrays.asList(
                IllusionerConfigs.class
        );
    }

    @Override
    @Nullable
    protected DisplayModule createDisplayModule() {
        return null;
    }
}
