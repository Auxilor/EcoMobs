package com.willfp.ecobosses.bosses.util.requirement.requirements;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.ecobosses.bosses.util.requirement.SpawnRequirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementPlaceholderEquals extends SpawnRequirement {
    /**
     * Create new requirement.
     */
    public RequirementPlaceholderEquals() {
        super("placeholder-equals");
    }

    @Override
    public boolean doesPlayerMeet(@NotNull final Player player,
                                  @NotNull final List<String> args) {
        String placeholder = args.get(0);
        String equals = args.get(1);

        return PlaceholderManager.translatePlaceholders(placeholder, player).equalsIgnoreCase(equals);
    }
}