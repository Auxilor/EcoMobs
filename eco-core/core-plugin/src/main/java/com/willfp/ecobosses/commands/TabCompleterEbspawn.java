package com.willfp.ecobosses.commands;

import com.willfp.eco.util.command.AbstractTabCompleter;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterEbspawn extends AbstractTabCompleter {
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
     * Instantiate a new tab-completer for /ebspawn.
     *
     * @param command Instance of /ebspawn.
     */
    public TabCompleterEbspawn(@NotNull final CommandEbspawn command) {
        super(command);
        reload();
    }

    /**
     * Called on reload.
     */
    @ConfigUpdater
    public static void reload() {
        BOSS_NAMES.clear();
        BOSS_NAMES.addAll(EcoBosses.values().stream().map(EcoBoss::getName).collect(Collectors.toList()));
    }

    /**
     * The execution of the tabcompleter.
     *
     * @param sender The sender of the command.
     * @param args   The arguments of the command.
     * @return A list of tab-completions.
     */
    @Override
    public List<String> onTab(@NotNull final CommandSender sender,
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
