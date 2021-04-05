package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.eco.util.LightningUtils;
import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectLightningNearbyEntities extends Effect {
    private final int frequency;
    private final double chance;
    private final double damage;
    private final double radius;

    public EffectLightningNearbyEntities(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 4) {
            showConfigError("Incorrect amount of arguments!");
        }

        frequency = Integer.parseInt(args.get(0));
        chance = Double.parseDouble(args.get(1));
        radius = Double.parseDouble(args.get(2));
        damage = Double.parseDouble(args.get(3));
    }

    @Override
    public String getUsage() {
        return "lightning-nearby-entities:<frequency>:<chance>:<damage>:<radius>";
    }

    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        if (tick % frequency == 0) {
            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                if (NumberUtils.randFloat(0, 100) < chance) {
                    if (nearbyEntity instanceof LivingEntity) {
                        LightningUtils.strike((LivingEntity) nearbyEntity, damage);
                    }
                }
            }
        }
    }
}
