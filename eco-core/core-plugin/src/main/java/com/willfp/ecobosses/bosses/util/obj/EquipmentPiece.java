package com.willfp.ecobosses.bosses.util.obj;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record EquipmentPiece(@NotNull ItemStack itemStack,
                             double chance) {
}
