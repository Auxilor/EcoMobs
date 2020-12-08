package com.willfp.illusioner.v1_16_R3;

import com.willfp.illusioner.nms.api.CooldownWrapper;
import org.bukkit.entity.Player;

public class Cooldown implements CooldownWrapper {
    @Override
    public double getAttackCooldown(Player player) {
        return player.getAttackCooldown();
    }
}
