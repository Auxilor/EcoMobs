package com.willfp.illusioner.command.commands;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.command.AbstractCommand;
import com.willfp.illusioner.util.internal.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unchecked")
public class CommandIldebug extends AbstractCommand {
    public CommandIldebug() {
        super("ildebug", "illusioner.debug", false);
    }

    @Override
    public void onExecute(CommandSender sender, List<String> args) {
        Logger.info("--------------- BEGIN DEBUG ----------------");
        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("Held Item: " + player.getInventory().getItemInMainHand().toString());
            Logger.info("");

            Logger.info("Held Item: " + player.getInventory().getItemInMainHand().toString());
            Logger.info("");
        }


        Logger.info("Running Version: " + IllusionerPlugin.getInstance().getDescription().getVersion());
        Logger.info("");

        Logger.info("Server Information: ");
        Logger.info("Players Online: " + Bukkit.getServer().getOnlinePlayers().size());
        Logger.info("Bukkit IP: " + Bukkit.getIp());
        Logger.info("Running Version: " + Bukkit.getVersion() + ", Bukkit Version: " + Bukkit.getBukkitVersion() + ", Alt Version: " + Bukkit.getServer().getVersion());
        Logger.info("Motd: " + Bukkit.getServer().getMotd());
        Logger.info("--------------- END DEBUG ----------------");
    }
}
