package com.willfp.ecobosses.bosses.util.requirement.requirements;

import com.willfp.ecobosses.bosses.util.requirement.SpawnRequirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementHasPermission extends SpawnRequirement {
    /**
     * Create new requirement.
     */
    public RequirementHasPermission() {
        super("has-permission");
    }

    @Override
    public boolean doesPlayerMeet(@NotNull final Player player,
                                  @NotNull final List<String> args) {
        String permission = args.get(0);
        return player.hasPermission(permission);
    }
}