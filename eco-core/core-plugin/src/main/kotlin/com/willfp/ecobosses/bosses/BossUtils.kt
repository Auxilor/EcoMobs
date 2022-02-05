package com.willfp.ecobosses.bosses

import com.willfp.ecobosses.EcoBossesPlugin
import com.willfp.libreforge.Holder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
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

private val spawnEggKey = EcoBossesPlugin.instance.namespacedKeyFactory.create("spawn_egg")

var ItemStack.bossEgg: EcoBoss?
    set(value) {
        val meta = this.itemMeta ?: return
        val pdc = meta.persistentDataContainer
        if (value == null) {
            pdc.remove(spawnEggKey)
        } else {
            pdc.set(spawnEggKey, PersistentDataType.STRING, value.id)
        }
        this.itemMeta = meta
    }
    get() {
        val meta = this.itemMeta ?: return null
        val pdc = meta.persistentDataContainer
        val id = pdc.get(spawnEggKey, PersistentDataType.STRING) ?: return null
        return Bosses.getByID(id)
    }
