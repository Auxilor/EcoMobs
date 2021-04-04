package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.command.AbstractTabCompleter;
import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandEbspawn extends AbstractCommand {
    /**
     * Instantiate a new executor for /ebspawn.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandEbspawn(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "ebspawn", "ecobosses.ebspawn", false);
    }

    @Override
    @Nullable
    public AbstractTabCompleter getTab() {
        return new TabCompleterEbspawn(this);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("specify-boss"));
            return;
        }

        String bossName = args.get(0);

        EcoBoss boss = EcoBosses.getByName(bossName.toLowerCase());

        if (boss == null) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("specify-boss"));
            return;
        }

        Location location = null;

        if (sender instanceof Player) {
            location = ((Player) sender).getLocation();
        }

        if (args.size() >= 4) {
            String xString = args.get(1);
            String yString = args.get(2);
            String zString = args.get(3);

            double xPos;
            double yPos;
            double zPos;

            if (xString.startsWith("~") && sender instanceof Player) {
                String xDiff = xString.replace("~", "");
                String yDiff = yString.replace("~", "");
                String zDiff = zString.replace("~", "");

                if (xDiff.isEmpty()) {
                    xPos = ((Player) sender).getLocation().getX();
                } else {
                    try {
                        xPos = ((Player) sender).getLocation().getX() + Double.parseDouble(xDiff);
                    } catch (NumberFormatException e) {
                        xPos = ((Player) sender).getLocation().getX();
                    }
                }

                if (yDiff.isEmpty()) {
                    yPos = ((Player) sender).getLocation().getY();
                } else {
                    try {
                        yPos = ((Player) sender).getLocation().getY() + Double.parseDouble(yDiff);
                    } catch (NumberFormatException e) {
                        yPos = ((Player) sender).getLocation().getY();
                    }
                }

                if (zDiff.isEmpty()) {
                    zPos = ((Player) sender).getLocation().getZ();
                } else {
                    try {
                        zPos = ((Player) sender).getLocation().getZ() + Double.parseDouble(yDiff);
                    } catch (NumberFormatException e) {
                        zPos = ((Player) sender).getLocation().getZ();
                    }
                }
            } else {
                try {
                    xPos = Double.parseDouble(xString);
                } catch (NumberFormatException e) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-location"));
                    return;
                }
                try {
                    yPos = Double.parseDouble(yString);
                } catch (NumberFormatException e) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-location"));
                    return;
                }
                try {
                    zPos = Double.parseDouble(zString);
                } catch (NumberFormatException e) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-location"));
                    return;
                }
            }

            location = new Location(null, xPos, yPos, zPos);
        }

        World world = null;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        }

        if (args.size() >= 5) {
            world = Bukkit.getWorld(args.get(4));
        }

        if (location == null) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-location"));
            return;
        }

        location.setWorld(world);

        if (world == null) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-world"));
            return;
        }

        boss.spawn(location);
        sender.sendMessage(this.getPlugin().getLangYml().getMessage("spawned"));
    }
}
