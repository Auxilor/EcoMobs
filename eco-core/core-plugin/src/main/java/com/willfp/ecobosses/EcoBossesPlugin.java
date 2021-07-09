package com.willfp.ecobosses;

import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.listeners.AttackListeners;
import com.willfp.ecobosses.bosses.listeners.AutoSpawnTimer;
import com.willfp.ecobosses.bosses.listeners.DeathListeners;
import com.willfp.ecobosses.bosses.listeners.PassiveListeners;
import com.willfp.ecobosses.bosses.listeners.SpawnListeners;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.commands.CommandEbdrop;
import com.willfp.ecobosses.commands.CommandEbkillall;
import com.willfp.ecobosses.commands.CommandEbreload;
import com.willfp.ecobosses.commands.CommandEbspawn;
import com.willfp.ecobosses.commands.TabCompleterEbspawn;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EcoBossesPlugin extends EcoPlugin {
    /**
     * Instance of the plugin.
     */
    @Getter
    private static EcoBossesPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoBossesPlugin() {
        super(86576, 10635, "com.willfp.ecobosses.proxy", "&9");
        instance = this;
    }

    /**
     * Code executed on plugin enable.
     */
    @Override
    public void enable() {
        this.getExtensionLoader().loadExtensions();

        if (this.getExtensionLoader().getLoadedExtensions().isEmpty()) {
            this.getLogger().info("&cNo extensions found");
        } else {
            this.getLogger().info("Extensions Loaded:");
            this.getExtensionLoader().getLoadedExtensions().forEach(extension -> this.getLogger().info("- " + extension.getName() + " v" + extension.getVersion()));
        }
    }

    /**
     * Code executed on plugin disable.
     */
    @Override
    public void disable() {
        this.getExtensionLoader().unloadExtensions();

        BossUtils.killAllBosses();
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
        this.getScheduler().runTimer(new AutoSpawnTimer(), 5, 1);
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
                new CommandEbspawn(this),
                new CommandEbkillall(this)
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
                new SpawnListeners(this),
                new PassiveListeners(this)
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
    protected String getMinimumEcoVersion() {
        return "5.7.1";
    }
}
