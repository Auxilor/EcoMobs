package com.willfp.ecobosses.bosses.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.effects.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectShuffleHotbar extends Effect {
    private final double chance;

    public EffectShuffleHotbar(@NotNull final List<String> args) {
        super(args);

        if (args.size() < 1) {
            showConfigError("Incorrect amount of arguments!");
        }

        chance = Double.parseDouble(args.get(0));
    }

    @Override
    public String getUsage() {
        return "shuffle-hotbar:<effect>:<chance>:<duration>:<strength>";
    }

    @Override
    public void onAttack(@NotNull final EcoBoss boss,
                         @NotNull final LivingEntity entity,
                         @NotNull final Player player) {
        if (NumberUtils.randFloat(0, 100) > this.chance) {
            return;
        }

        List<ItemStack> hotbar = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            hotbar.add(player.getInventory().getItem(i));
        }
        Collections.shuffle(hotbar);
        int i2 = 0;
        for (ItemStack item : hotbar) {
            player.getInventory().setItem(i2, item);
            i2++;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1, 0.5f);
    }
}
