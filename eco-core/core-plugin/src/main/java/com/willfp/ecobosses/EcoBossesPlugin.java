package com.willfp.ecobosses;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecobosses.bosses.listeners.AttackListeners;
import com.willfp.ecobosses.bosses.listeners.AutoSpawnTimer;
import com.willfp.ecobosses.bosses.listeners.DeathListeners;
import com.willfp.ecobosses.bosses.listeners.PassiveListeners;
import com.willfp.ecobosses.bosses.listeners.SpawnListeners;
import com.willfp.ecobosses.bosses.util.BossUtils;
import com.willfp.ecobosses.commands.CommandEcobosses;
import com.willfp.ecobosses.integrations.levelledmobs.LevelledMobsListener;
import com.willfp.ecobosses.util.DiscoverRecipeListener;
import lombok.Getter;
import org.bukkit.event.Listener;

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
        super(525, 10635, "com.willfp.ecobosses.proxy", "&9");
        instance = this;
    }

    @Override
    protected void handleDisable() {
        BossUtils.killAllBosses();
    }

    @Override
    protected void handleReload() {
        this.getScheduler().runTimer(new AutoSpawnTimer(), 5, 1);
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcobosses(this)
        );
    }

    @Override
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return Arrays.asList(
                new IntegrationLoader("LevelledMobs", () -> this.getEventManager().registerListener(new LevelledMobsListener()))
        );
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new AttackListeners(this),
                new DeathListeners(this),
                new SpawnListeners(this),
                new DiscoverRecipeListener(this),
                new PassiveListeners(this)
        );
    }
}
