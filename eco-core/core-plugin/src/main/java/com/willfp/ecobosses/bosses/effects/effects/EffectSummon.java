package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import com.willfp.ecobosses.bosses.util.bosstype.BossEntityUtils;
import com.willfp.ecobosses.bosses.util.bosstype.BossType;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectSummon extends Effect {
    private final BossType type;
    private final double chance;

    public EffectSummon(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 2) {
            showConfigError("Incorrect amount of arguments!");
        }

        type = BossEntityUtils.getBossType(args.get(0));
        chance = Double.parseDouble(args.get(1));
    }

    @Override
    public String getUsage() {
        return "summon:<entity>:<chance>";
    }

    @Override
    public void onAttack(@NotNull final EcoBoss boss,
                         @NotNull final LivingEntity entity,
                         @NotNull final Player player) {
        if (NumberUtils.randFloat(0, 100) > this.chance) {
            return;
        }

        Location loc = player.getLocation().add(NumberUtils.randInt(2, 6), 0, NumberUtils.randInt(2, 6));
        for (int i = 0; i < 15; i++) {
            if (loc.getBlock().getType() == Material.AIR) {
                break;
            }

            loc.add(0, 1, 0);
        }

        Entity summonedEntity = type.spawnBossEntity(loc);
        if (summonedEntity instanceof Mob) {
            ((Mob) summonedEntity).setTarget(player);
        }

        for (OptionedSound sound : boss.getSummonSounds()) {
            player.getWorld().playSound(entity.getLocation(), sound.sound(), sound.volume(), sound.pitch());
        }
    }
}
