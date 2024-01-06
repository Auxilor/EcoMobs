package com.willfp.ecomobs.mob.impl

import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.placeholder.MobPlaceholders
import com.willfp.ecomobs.mob.placeholder.formatMobPlaceholders
import com.willfp.ecomobs.tick.TickHandler
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

internal class LivingMobImpl(
    private val plugin: EcoMobsPlugin,
    override val mob: EcoMob,
    override val entity: Mob,
    private val trackingRemovalCallback: () -> Unit
) : LivingMob {
    private val ticker = plugin.runnableFactory.create {
        tick(tick)
        tick++

        if (!isAlive) {
            it.cancel()
            handleRemove()
        }
    }

    private var isRunning = false

    private var tick = 0

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

    override fun handleEvent(event: MobEvent, trigger: DispatchedTrigger) {
        for (placeholder in MobPlaceholders.values()) {
            trigger.addPlaceholder(NamedValue(placeholder.id, placeholder.getValue(this)))
        }

        for (placeholder in plugin.topDamagerHandler.generatePlaceholders(entity)) {
            trigger.addPlaceholder(placeholder)
        }

        mob.handleEvent(event, trigger)
    }

    override fun kill(player: Player?, removeTracking: Boolean) {
        handleRemove(removeTracking = removeTracking)

        mob.spawnDrops(entity.location, player)
    }

    override fun despawn() {
        entity.remove()
        handleRemove()

        Bukkit.getPluginManager().callEvent(
            EcoMobDespawnEvent(this)
        )
    }

    private fun handleRemove(removeTracking: Boolean = true) {
        ticker.cancel()
        if (removeTracking) {
            trackingRemovalCallback()
        }

        for (handler in this.tickHandlers) {
            handler.onRemove(this, tick)
        }
    }
}
