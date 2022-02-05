package com.willfp.ecobosses.bosses

import com.willfp.libreforge.Holder
import org.bukkit.entity.Player
import kotlin.math.pow

val Player.bossHolders: Iterable<Holder>
    get() {
        val holders = mutableListOf<Holder>()

        for (boss in Bosses.values()) {
            for (entity in boss.getAllAlive()) {
                if (entity.world != this.world) {
                    continue
                }

                if (entity.location.distanceSquared(this.location) <= boss.influenceRadius.pow(2)) {
                    holders.add(boss)
                }
            }
        }

        return holders
    }