package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.EcoBosses;
import org.bukkit.Location;

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

            if (tick % boss.getAutoSpawnInterval() == 0) {
                Location location = boss.getAutoSpawnLocations().get(NumberUtils.randInt(0, boss.getAutoSpawnLocations().size() - 1));
                boss.spawn(location);
            }
        }
    }
}
