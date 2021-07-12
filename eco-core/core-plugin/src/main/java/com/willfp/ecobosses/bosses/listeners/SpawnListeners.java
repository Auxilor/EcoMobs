package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.util.obj.SpawnTotem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class SpawnListeners extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Create new spawn listeners and link them to a plugin.
     *
     * @param plugin The plugin to link to.
     */
    public SpawnListeners(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Called on block place.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void spawnTotem(@NotNull final BlockPlaceEvent event) {
        for (int i = 0; i < 3; i++) {
            Block block1;
            Block block2;
            Block block3;

            if (i == 0) {
                block3 = event.getBlock();
                block2 = event.getBlock().getRelative(0, -1, 0);
                block1 = event.getBlock().getRelative(0, -2, 0);
            } else if (i == 1) {
                block1 = event.getBlock();
                block2 = event.getBlock().getRelative(0, 1, 0);
                block3 = event.getBlock().getRelative(0, 2, 0);
            } else {
                block2 = event.getBlock();
                block1 = event.getBlock().getRelative(0, -1, 0);
                block3 = event.getBlock().getRelative(0, 1, 0);
            }

            SpawnTotem placedTotem = new SpawnTotem(block1.getType(), block2.getType(), block3.getType());

            for (EcoBoss boss : EcoBosses.values()) {
                if (boss.isSpawnTotemEnabled()) {
                    if (!boss.getSpawnTotemDisabledWorldNames().contains(event.getBlock().getWorld().getName().toLowerCase())) {
                        if (boss.getSpawnTotem().equals(placedTotem)) {
                            block1.setType(Material.AIR);
                            block2.setType(Material.AIR);
                            block3.setType(Material.AIR);

                            boss.spawn(event.getBlock().getLocation());
                        }
                    }
                }
            }
        }
    }
}
