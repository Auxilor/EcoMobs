package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import com.willfp.ecobosses.bosses.util.BossUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AutoSpawnTimer implements Runnable {
    private int tick = 0;

    @Override
    public void run() {
        tick++;

        for (EcoBoss boss : EcoBosses.values()) {
            if (boss.getAutoSpawnInterval() < 0) {
                continue;
            }

            if (boss.getAutoSpawnLocations().isEmpty()) {
                continue;
            }

            Set<World> worlds = new HashSet<>();

            for (UUID uuid : boss.getLivingBosses().keySet()) {
                Entity entity = Bukkit.getEntity(uuid);

                BossUtils.warnIfNull(entity);
                assert entity != null;

                worlds.add(entity.getWorld());
            }

            List<Location> locations = new ArrayList<>(boss.getAutoSpawnLocations());
            locations.removeIf(location -> worlds.contains(location.getWorld()));

            if (locations.isEmpty()) {
                continue;
            }

            if (tick % boss.getAutoSpawnInterval() == 0) {
                Location location = locations.get(NumberUtils.randInt(0, locations.size() - 1));
                boss.spawn(location);
            }
        }
    }
}
