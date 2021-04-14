package com.willfp.ecobosses.bosses.effects;

import com.willfp.ecobosses.bosses.effects.effects.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class Effects {
    /**
     * Registered effects.
     */
    private static final Map<String, Function<List<String>, Effect>> EFFECTS = new HashMap<>();

    static {
        register("damage-nearby-players", EffectDamageNearbyPlayers::new);
        register("lightning-nearby-entities", EffectLightningNearbyEntities::new);
        register("summon", EffectSummon::new);
        register("give-potion-effect", EffectGivePotionEffect::new);
        register("shuffle-hotbar", EffectShuffleHotbar::new);
        register("teleport", EffectTeleport::new);
    }

    /**
     * Register new effect.
     *
     * @param name    The effect name.
     * @param creator Function to create an instance of the effect given args.
     */
    public void register(@NotNull final String name,
                         @NotNull final Function<List<String>, Effect> creator) {
        EFFECTS.put(name, creator);
    }

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
        Function<List<String>, Effect> found = EFFECTS.get(name.toLowerCase());
        return found == null ? null : found.apply(args);
    }
}
