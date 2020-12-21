package com.willfp.illusioner.integrations.ecoenchants;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EcoEnchantsManager {
    private static final Set<EcoEnchantsIntegration> integrations = new HashSet<>();

    public static void register(EcoEnchantsIntegration integration) {
        integrations.add(integration);
    }

    public static void dropQueueItems(Player player, Location location, ItemStack... items) {
        Arrays.stream(items).forEach(item -> integrations.forEach(integration -> integration.dropItems(player, location, item)));
    }

    public static void dropQueueExp(Player player, Location location, double amount) {
        integrations.forEach(integration -> integration.dropExp(player, location, amount));
    }

    public static boolean isRegistered() {
        return !integrations.isEmpty();
    }
}
