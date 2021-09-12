package com.willfp.ecobosses.bosses.util.requirement;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SpawnRequirement {
    /**
     * The ID of the requirement.
     */
    @Getter
    private final String id;

    protected SpawnRequirement(@NotNull final String id) {
        this.id = id;

        SpawnRequirements.addNewRequirement(this);
    }

    /**
     * Test if the player meets the requirement.
     *
     * @param player The player.
     * @param args   The arguments.
     * @return The requirement.
     */
    public abstract boolean doesPlayerMeet(@NotNull Player player,
                                           @NotNull List<String> args);
}