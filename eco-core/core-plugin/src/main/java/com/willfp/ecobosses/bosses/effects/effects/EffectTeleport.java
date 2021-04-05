package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectTeleport extends Effect {
    private final int range;
    private final double chance;

    public EffectTeleport(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 2) {
            showConfigError("Incorrect amount of arguments!");
        }

        range = Integer.parseInt(args.get(0));
        chance = Double.parseDouble(args.get(1));
    }

    @Override
    public String getUsage() {
        return "teleport:<range>:<chance>";
    }

    @Override
    public void onAttack(@NotNull final EcoBoss boss,
                         @NotNull final LivingEntity entity,
                         @NotNull final Player player) {
        if (NumberUtils.randFloat(0, 100) > this.chance) {
            return;
        }

        List<Location> valid = new ArrayList<>();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Location location = entity.getLocation().clone();
                    location.setX(location.getX() + x);
                    location.setY(location.getY() + y);
                    location.setZ(location.getZ() + z);

                    Block block = location.getBlock();

                    if (block.getType() == Material.AIR
                            && block.getRelative(BlockFace.UP).getType() == Material.AIR
                            && !(block.getRelative(BlockFace.DOWN).isLiquid() || block.getRelative(BlockFace.DOWN).getType() == Material.AIR)) {
                        valid.add(location);
                    }
                }
            }
        }

        if (valid.isEmpty()) {
            return;
        }

        Collections.shuffle(valid);
        Location location = valid.get(0);

        entity.teleport(location);
    }
}
