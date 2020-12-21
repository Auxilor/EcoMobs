package com.willfp.illusioner.integrations.ecoenchants.plugins;

import com.willfp.ecoenchants.util.internal.DropQueue;
import com.willfp.illusioner.integrations.ecoenchants.EcoEnchantsIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EcoEnchantsIntegrationImpl implements EcoEnchantsIntegration {
    @Override
    public void dropItems(Player player, Location location, ItemStack itemStack) {
        new DropQueue(player)
                .setLocation(location)
                .addItem(itemStack)
                .push();
    }

    @Override
    public void dropExp(Player player, Location location, double amount) {
        new DropQueue(player)
                .setLocation(location)
                .addXP((int) Math.ceil(amount))
                .push();
    }

    @Override
    public String getPluginName() {
        return "EcoEnchants";
    }
}
