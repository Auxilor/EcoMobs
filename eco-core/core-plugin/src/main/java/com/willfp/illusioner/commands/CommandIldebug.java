package com.willfp.illusioner.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.illusioner.IllusionerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandIldebug extends AbstractCommand {
    /**
     * Instantiate a new executor for /ildebug.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandIldebug(@NotNull final IllusionerPlugin plugin) {
        super(plugin, "ildebug", "illusioner.debug", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        this.getPlugin().getLog().info("--------------- BEGIN DEBUG ----------------");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("Held Item: " + player.getInventory().getItemInMainHand().toString());
            this.getPlugin().getLog().info("");

            this.getPlugin().getLog().info("Held Item: " + player.getInventory().getItemInMainHand().toString());
            this.getPlugin().getLog().info("");
        }

        this.getPlugin().getLog().info("Options: " + IllusionerManager.OPTIONS.toString());

        this.getPlugin().getLog().info("Running Version: " + IllusionerPlugin.getInstance().getDescription().getVersion());
        this.getPlugin().getLog().info("");

        this.getPlugin().getLog().info("Server Information: ");
        this.getPlugin().getLog().info("Players Online: " + Bukkit.getServer().getOnlinePlayers().size());
        this.getPlugin().getLog().info("Bukkit IP: " + Bukkit.getIp());
        this.getPlugin().getLog().info("Running Version: " + Bukkit.getVersion() + ", Bukkit Version: " + Bukkit.getBukkitVersion() + ", Alt Version: " + Bukkit.getServer().getVersion());
        this.getPlugin().getLog().info("Motd: " + Bukkit.getServer().getMotd());
        this.getPlugin().getLog().info("--------------- END DEBUG ----------------");
    }
}
