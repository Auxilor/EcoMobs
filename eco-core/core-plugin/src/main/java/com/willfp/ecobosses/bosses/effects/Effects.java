package com.willfp.ecobosses.bosses.effects;

import com.google.common.collect.ImmutableMap;
import com.willfp.ecobosses.bosses.effects.effects.EffectDamageNearbyPlayers;
import com.willfp.ecobosses.bosses.effects.effects.EffectGivePotionEffect;
import com.willfp.ecobosses.bosses.effects.effects.EffectLightningNearbyEntities;
import com.willfp.ecobosses.bosses.effects.effects.EffectShuffleHotbar;
import com.willfp.ecobosses.bosses.effects.effects.EffectSummon;
import com.willfp.ecobosses.bosses.effects.effects.EffectTeleport;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class Effects {
    /**
     * Registered effects.
     */
    private static final Map<String, Function<List<String>, Effect>> EFFECTS = new ImmutableMap.Builder<String, Function<List<String>, Effect>>()
            .put("damage-nearby-players", EffectDamageNearbyPlayers::new)
            .put("lightning-nearby-entities", EffectLightningNearbyEntities::new)
            .put("summon", EffectSummon::new)
            .put("give-potion-effect", EffectGivePotionEffect::new)
            .put("shuffle-hotbar", EffectShuffleHotbar::new)
            .put("teleport", EffectTeleport::new)
            .build();

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
