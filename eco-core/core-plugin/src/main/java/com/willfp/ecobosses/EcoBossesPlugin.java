package com.willfp.ecobosses;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.display.DisplayModule;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.listeners.AttackListeners;
import com.willfp.ecobosses.bosses.listeners.DeathListeners;
import com.willfp.ecobosses.bosses.listeners.SpawnListeners;
import com.willfp.ecobosses.commands.CommandEbdrop;
import com.willfp.ecobosses.commands.CommandEbreload;
import com.willfp.ecobosses.commands.CommandEbspawn;
import com.willfp.ecobosses.commands.TabCompleterEbspawn;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EcoBossesPlugin extends AbstractEcoPlugin {
    /**
     * Instance of the plugin.
     */
    @Getter
    private static EcoBossesPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoBossesPlugin() {
        super("EcoBosses", 86576, 10635, "com.willfp.ecobosses.proxy", "&9");
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
                new CommandEbreload(this),
                new CommandEbdrop(this),
                new CommandEbspawn(this)
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
                new AttackListeners(this),
                new DeathListeners(this),
                new SpawnListeners(this)
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return Arrays.asList(
                EcoBosses.class,
                TabCompleterEbspawn.class
        );
    }

    @Override
    @Nullable
    protected DisplayModule createDisplayModule() {
        return null;
    }
}
