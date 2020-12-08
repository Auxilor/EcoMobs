package com.willfp.illusioner.illusioner;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.nms.NMSIllusioner;
import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnListeners implements Listener {
    @EventHandler
    public void onSpawn(BlockPlaceEvent event) {
        if(!event.getBlock().getType().equals(Material.DRAGON_HEAD))
            return;
        if(!event.getBlock().getLocation().add(0,-1,0).getBlock().getType().equals(Material.BEACON))
            return;
        if(!event.getBlock().getLocation().add(0,-2,0).getBlock().getType().equals(Material.DIAMOND_BLOCK))
            return;

        try {
            event.getBlock().getLocation().getBlock().setType(Material.AIR);
            event.getBlock().getLocation().add(0, -1, 0).getBlock().setType(Material.AIR);
            event.getBlock().getLocation().add(0, -2, 0).getBlock().setType(Material.AIR);

            event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1000, 0.5f);
            event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_WITHER_SPAWN, 1000, 2f);

            EntityIllusionerWrapper illusioner = NMSIllusioner.spawn(event.getBlock().getLocation(), 600, 50);
            illusioner.createBossbar(IllusionerPlugin.getInstance(), BarColor.BLUE, BarStyle.SOLID);
        } catch(Exception ignored) {}
    }
}
