package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectDamageNearbyPlayers extends Effect {
    private final int frequency;
    private final double damage;
    private final double radius;

    public EffectDamageNearbyPlayers(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 3) {
            showConfigError("Incorrect amount of arguments!");
        }

        frequency = Integer.parseInt(args.get(0));
        radius = Double.parseDouble(args.get(1));
        damage = Double.parseDouble(args.get(2));
    }

    @Override
    public String getUsage() {
        return "damage-nearby-players:<frequency>:<damage>:<radius>";
    }

    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        if (tick % frequency == 0) {
            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                if (nearbyEntity instanceof Player) {
                    ((Player) nearbyEntity).damage(damage, entity);
                }
            }
        }
    }
}
