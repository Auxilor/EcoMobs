package com.willfp.illusioner.integrations.ecoenchants;

import com.willfp.illusioner.integrations.Integration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface EcoEnchantsIntegration extends Integration {
    void dropItems(Player player, Location location, ItemStack itemStack);
    void dropExp(Player player, Location location, double amount);
}
