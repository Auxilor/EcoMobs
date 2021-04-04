package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CommandEbkillall extends AbstractCommand {
    /**
     * Instantiate a new executor for /ebspawn.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEbkillall(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ebkillall", "ecobosses.killall", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        int amount = 0;
        for (EcoBoss boss : EcoBosses.values()) {
            for (UUID uuid : boss.getLivingBosses().keySet()) {
                Entity entity = Bukkit.getEntity(uuid);
                boss.removeLivingBoss(uuid);
                if (entity == null) {
                    break;
                }
                entity.remove();
                amount++;
            }
        }

        sender.sendMessage(this.getPlugin().getLangYml().getMessage("killall").replace("%amount%", String.valueOf(amount)));
    }
}
