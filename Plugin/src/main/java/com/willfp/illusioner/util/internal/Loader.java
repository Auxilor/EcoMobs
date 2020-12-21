package com.willfp.illusioner.util.internal;

import com.willfp.ecoenchants.util.interfaces.Callable;
import com.willfp.ecoenchants.util.optional.Prerequisite;
import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.command.commands.CommandIldebug;
import com.willfp.illusioner.command.commands.CommandIlreload;
import com.willfp.illusioner.config.ConfigManager;
import com.willfp.illusioner.events.armorequip.ArmorListener;
import com.willfp.illusioner.events.armorequip.DispenserArmorListener;
import com.willfp.illusioner.events.entitydeathbyentity.EntityDeathByEntityListeners;
import com.willfp.illusioner.events.naturalexpgainevent.NaturalExpGainListeners;
import com.willfp.illusioner.illusioner.IllusionerManager;
import com.willfp.illusioner.illusioner.listeners.AttackListeners;
import com.willfp.illusioner.illusioner.listeners.DeathListeners;
import com.willfp.illusioner.illusioner.listeners.SpawnListeners;
import com.willfp.illusioner.integrations.ecoenchants.EcoEnchantsManager;
import com.willfp.illusioner.integrations.ecoenchants.plugins.EcoEnchantsIntegrationImpl;
import com.willfp.illusioner.nms.BlockBreak;
import com.willfp.illusioner.nms.Cooldown;
import com.willfp.illusioner.nms.NMSIllusioner;
import com.willfp.illusioner.nms.OpenInventory;
import com.willfp.illusioner.nms.TridentStack;
import com.willfp.illusioner.util.internal.updater.PlayerJoinListener;
import com.willfp.illusioner.util.internal.updater.UpdateChecker;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class containing methods for the loading and unloading of Illusioner
 */
public class Loader {

    /**
     * Called by {@link IllusionerPlugin#onEnable()}
     */
    public static void load() {
        Logger.info("==========================================");
        Logger.info("");
        Logger.info("Loading &9Illusioner");
        Logger.info("Made by &9Auxilor&f - willfp.com");
        Logger.info("");
        Logger.info("==========================================");

        /*
        Load Configs
         */

        Logger.info("Loading Configs...");
        ConfigManager.updateConfigs();
        Logger.info("");

        /*
        Load NMS
         */

        Logger.info("Loading NMS APIs...");

        if (Cooldown.init()) {
            Logger.info("Cooldown: &aSUCCESS");
        } else {
            Logger.info("Cooldown: &cFAILURE");
            Logger.error("&cAborting...");
            Bukkit.getPluginManager().disablePlugin(IllusionerPlugin.getInstance());
        }

        if (TridentStack.init()) {
            Logger.info("Trident API: &aSUCCESS");
        } else {
            Logger.info("Trident API: &cFAILURE");
            Logger.error("&cAborting...");
            Bukkit.getPluginManager().disablePlugin(IllusionerPlugin.getInstance());
        }

        if (BlockBreak.init()) {
            Logger.info("Block Break: &aSUCCESS");
        } else {
            Logger.info("Block Break: &cFAILURE");
            Logger.error("&cAborting...");
            Bukkit.getPluginManager().disablePlugin(IllusionerPlugin.getInstance());
        }

        if (OpenInventory.init()) {
            Logger.info("Open Inventory: &aSUCCESS");
        } else {
            Logger.info("Open Inventory: &cFAILURE");
            Logger.error("&cAborting...");
            Bukkit.getPluginManager().disablePlugin(IllusionerPlugin.getInstance());
        }

        if (NMSIllusioner.init()) {
            Logger.info("NMS Illusioner: &aSUCCESS");
        } else {
            Logger.info("NMS Illusioner: &cFAILURE");
            Logger.error("&cAborting...");
            Bukkit.getPluginManager().disablePlugin(IllusionerPlugin.getInstance());
        }

        Logger.info("");

        /*
        Register Integrations
         */
        Logger.info("Loading Integrations...");

        final HashMap<String, Callable> integrations = new HashMap<String, Callable>() {{
            put("EcoEnchants", () -> EcoEnchantsManager.register(new EcoEnchantsIntegrationImpl()));
        }};

        Set<String> enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.toSet());

        integrations.forEach(((s, callable) -> {
            StringBuilder log = new StringBuilder();
            log.append(s).append(": ");
            if (enabledPlugins.contains(s)) {
                callable.call();
                log.append("&aENABLED");
            } else {
                log.append("&9DISABLED");
            }
            Logger.info(log.toString());
        }));

        Prerequisite.update();
        Logger.info("");

        /*
        Register Events
         */

        Logger.info("Registering Events...");
        Bukkit.getPluginManager().registerEvents(new ArmorListener(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new DispenserArmorListener(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityDeathByEntityListeners(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new NaturalExpGainListeners(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new AttackListeners(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new DeathListeners(), IllusionerPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpawnListeners(), IllusionerPlugin.getInstance());
        Logger.info("");

        /*
        Register options
         */
        Logger.info("Loading options...");
        IllusionerManager.OPTIONS.reload();
        Logger.info("");

        /*
        Load Commands
         */

        Logger.info("Loading Commands...");
        new CommandIlreload().register();
        new CommandIldebug().register();
        Logger.info("");
        
        /*
        Start bStats
         */

        Logger.info("Hooking into bStats...");
        new Metrics(IllusionerPlugin.getInstance(), 9596);
        Logger.info("");

        /*
        Finish
         */

        Bukkit.getScheduler().runTaskLater(IllusionerPlugin.getInstance(), Loader::postLoad, 1);

        Logger.info("Loaded &9Illusioner!");
    }

    /**
     * Called after server is loaded
     */
    public static void postLoad() {
        new UpdateChecker(IllusionerPlugin.getInstance(), 86576).getVersion((version) -> {
            DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(IllusionerPlugin.getInstance().getDescription().getVersion());
            DefaultArtifactVersion mostRecentVersion = new DefaultArtifactVersion(version);
            Logger.info("----------------------------");
            Logger.info("");
            Logger.info("Illusioner Updater");
            Logger.info("");
            if (currentVersion.compareTo(mostRecentVersion) > 0 || currentVersion.equals(mostRecentVersion)) {
                Logger.info("&aIllusioner is up to date! (Version " + IllusionerPlugin.getInstance().getDescription().getVersion() + ")");
            } else {
                UpdateChecker.setOutdated(true);
                UpdateChecker.setNewVersion(version);

                Bukkit.getScheduler().runTaskTimer(IllusionerPlugin.getInstance(), () -> {
                    Logger.info("&6Illusioner is out of date! (Version " + IllusionerPlugin.getInstance().getDescription().getVersion() + ")");
                    Logger.info("&6The newest version is &f" + version);
                    Logger.info("&6Download the new version here: &fhttps://www.spigotmc.org/resources/illusioner.79573/");
                }, 0, 864000);
            }
            Logger.info("");
            Logger.info("----------------------------");
        });

        Logger.info("");
    }

    /**
     * Called by {@link IllusionerPlugin#onDisable()}
     */
    public static void unload() {
        Logger.info("&cDisabling Illusioner...");
        Logger.info("&fBye! :)");
    }

    /**
     * Called by /ecoreload
     */
    public static void reload() {
        ConfigManager.updateConfigs();
        IllusionerManager.OPTIONS.reload();
    }
}
