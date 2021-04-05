package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectGivePotionEffect extends Effect {
    private final PotionEffectType type;
    private final double chance;
    private final int duration;
    private final int strength;

    public EffectGivePotionEffect(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 4) {
            showConfigError("Incorrect amount of arguments!");
        }

        type = PotionEffectType.getByName(args.get(0).toUpperCase());
        chance = Double.parseDouble(args.get(1));
        duration = Integer.parseInt(args.get(2));
        strength = Integer.parseInt(args.get(3));
    }

    @Override
    public String getUsage() {
        return "give-potion-effect:<effect>:<chance>:<duration>:<strength>";
    }

    @Override
    public void onAttack(@NotNull final EcoBoss boss,
                         @NotNull final LivingEntity entity,
                         @NotNull final Player player) {
        if (NumberUtils.randFloat(0, 100) > this.chance) {
            return;
        }

        player.addPotionEffect(new PotionEffect(type, duration, strength - 1));
    }
}
