package com.willfp.illusioner.illusioner.listeners;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.illusioner.BlockStructure;
import com.willfp.illusioner.illusioner.IllusionerManager;
import com.willfp.illusioner.nms.NMSIllusioner;
import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

public class SpawnListeners implements Listener {
    @EventHandler
    public void onSpawn(BlockPlaceEvent event) {
        boolean matches = false;
        Set<Block> match = new HashSet<>();
        for(int i = 0; i < 3; i++) {
            Block block1 = event.getBlock().getRelative(0, i, 0);
            Block block2 = event.getBlock().getRelative(0, -1 + i, 0);
            Block block3 = event.getBlock().getRelative(0, -2 + i, 0);
            matches = BlockStructure.matches(new BlockStructure(block1.getType(), block2.getType(), block3.getType()));
            if(matches) {
                match.add(block1);
                match.add(block2);
                match.add(block3);
                break;
            }
        }

        if(!matches)
            return;

        match.forEach(block -> block.setType(Material.AIR));
        IllusionerManager.OPTIONS.getSpawnSounds().forEach(optionedSound -> {
            if(optionedSound.isBroadcast()) {
                event.getBlock().getWorld().playSound(event.getBlock().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
            } else {
                event.getPlayer().playSound(event.getBlock().getLocation(), optionedSound.getSound(), optionedSound.getVolume(), optionedSound.getPitch());
            }
        });

        EntityIllusionerWrapper illusioner = NMSIllusioner.spawn(event.getBlock().getLocation(), IllusionerManager.OPTIONS.getMaxHealth(), IllusionerManager.OPTIONS.getAttackDamage(), IllusionerManager.OPTIONS.getName());
        illusioner.createBossbar(IllusionerPlugin.getInstance(), IllusionerManager.OPTIONS.getColor(), IllusionerManager.OPTIONS.getStyle());
    }
}
