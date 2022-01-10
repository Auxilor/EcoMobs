package com.willfp.ecobosses.commands;

import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSpawn extends Subcommand {
    /**
     * The cached names.
     */
    private static final List<String> BOSS_NAMES = new ArrayList<>();

    /**
     * The cached numbers.
     */
    private static final List<String> TILDE = Arrays.asList(
            "~"
    );

    /**
     * Instantiate a new executor for /ebspawn.
     *
     * @param plugin The plugin to manage the execution for.
     */
    public CommandSpawn(@NotNull final EcoBossesPlugin plugin) {
        super(plugin, "spawn", "ecobosses.command.spawn", false);
        reload();
    }

    /**
     * Called on reload.
     */
    @ConfigUpdater
    public static void reload() {
        BOSS_NAMES.clear();
        BOSS_NAMES.addAll(EcoBosses.values().stream().map(EcoBoss::getId).collect(Collectors.toList()));
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
                    yPos = Double.parseDouble(yString);
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

    @Override
    public List<String> tabComplete(@NotNull final CommandSender sender,
                                    @NotNull final List<String> args) {
        List<String> completions = new ArrayList<>();

        if (args.isEmpty()) {
            // Currently, this case is not ever reached
            return new ArrayList<>();
        }

        if (args.size() == 1) {
            StringUtil.copyPartialMatches(args.get(0), BOSS_NAMES, completions);

            Collections.sort(completions);
            return completions;
        }

        if (args.size() == 2) {
            StringUtil.copyPartialMatches(args.get(1), TILDE, completions);

            Collections.sort(completions);
            return completions;
        }

        if (args.size() == 3) {
            StringUtil.copyPartialMatches(args.get(2), TILDE, completions);

            Collections.sort(completions);
            return completions;
        }

        if (args.size() == 4) {
            StringUtil.copyPartialMatches(args.get(3), TILDE, completions);

            Collections.sort(completions);
            return completions;
        }

        if (args.size() == 5) {
            StringUtil.copyPartialMatches(args.get(4), Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()), completions);

            Collections.sort(completions);
            return completions;
        }

        return new ArrayList<>(0);
    }
}
