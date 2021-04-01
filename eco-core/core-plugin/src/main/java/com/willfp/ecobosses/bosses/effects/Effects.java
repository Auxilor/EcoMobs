package com.willfp.ecobosses.bosses.effects;

import com.willfp.ecobosses.bosses.effects.effects.DamageNearbyPlayers;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class Effects {
    /**
     * Get effect matching name.
     *
     * @param name The effect name.
     * @param args The args.
     * @return The found effect, or null.
     */
    @Nullable
    public Effect getEffect(@NotNull final String name,
                            @NotNull final List<String> args) {
        switch (name.toLowerCase()) {
            case "damage-nearby-players":
                return new DamageNearbyPlayers(args);

            default:
                return null;
        }
    }
}
