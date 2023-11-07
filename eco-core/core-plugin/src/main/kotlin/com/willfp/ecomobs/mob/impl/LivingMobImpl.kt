package com.willfp.ecomobs.mob.impl

import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.formatMobPlaceholders
import com.willfp.ecomobs.tick.TickHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

internal class LivingMobImpl(
    plugin: EcoMobsPlugin,
    override val mob: EcoMob,
    override val entity: Mob,
    private val deathCallback: () -> Unit
) : LivingMob {
    private val ticker = plugin.runnableFactory.create {
        tick(tick)
        tick++

        if (!isAlive) {
            it.cancel()
            remove()
        }
    }

    private var isRunning = false

    private var tick = 1

    private val tickHandlers = mutableListOf<TickHandler>()

    override val isAlive: Boolean
        get() = entity.isValid

    override val displayName: String
        get() = mob.rawDisplayName.formatMobPlaceholders(this).formatEco()

    override val ticksLeft: Int
        get() = mob.lifespan - tick

    fun addTickHandler(handler: TickHandler) {
        tickHandlers += handler
    }

    private fun tick(tick: Int) {
        for (handler in tickHandlers) {
            handler.tick(this, tick)
        }
    }

    fun startTicking() {
        if (isRunning) {
            throw IllegalStateException("Ticking already started")
        }

        isRunning = true
        ticker.runTaskTimer(1, 1)
    }

    override fun kill(player: Player?) {
        remove()

        mob.spawnDrops(entity.location, player)
    }

    override fun despawn() {
        remove()

        Bukkit.getPluginManager().callEvent(
            EcoMobDespawnEvent(this)
        )
    }

    private fun remove() {
        ticker.cancel()

        entity.remove()
        deathCallback()

        for (handler in this.tickHandlers) {
            handler.onRemove(this, tick)
        }
    }
}
