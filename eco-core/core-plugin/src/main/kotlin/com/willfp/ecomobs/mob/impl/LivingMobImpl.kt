package com.willfp.ecomobs.mob.impl

import com.willfp.eco.util.formatEco
import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.placeholder.MobPlaceholders
import com.willfp.ecomobs.mob.placeholder.formatMobPlaceholders
import com.willfp.ecomobs.mob.stage.DamageStage
import com.willfp.ecomobs.mob.stage.DamageStageMode
import com.willfp.ecomobs.mob.stage.DamageStageTracker
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.tick.TickHandler
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

internal class LivingMobImpl(
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

    internal val stageTracker = if (mob.usesDamageStages) {
        DamageStageTracker(mob.damageStages, ::triggerStageEffects)
    } else {
        null
    }

    override val damageStage: DamageStage?
        get() = stageTracker?.stage

    override val damageStageNumber: Int
        get() = stageTracker?.stageNumber ?: 0

    override val damageStageProgress: Double
        get() = stageTracker?.stageProgress ?: 1.0

    override val hitsRemaining: Double
        get() = stageTracker
            ?.takeIf { it.stage.mode == DamageStageMode.HITS }
            ?.remaining
            ?: 0.0

    // Fix for drops being sent twice
    private var hasBeenKilled = false

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
        addMobPlaceholders(trigger)
        mob.handleEvent(event, trigger)
    }

    private fun addMobPlaceholders(trigger: DispatchedTrigger) {
        for (placeholder in MobPlaceholders.values()) {
            trigger.addPlaceholder(NamedValue(placeholder.id, placeholder.getValue(this)))
        }

        for (placeholder in plugin.topDamagerHandler.generatePlaceholders(entity)) {
            trigger.addPlaceholder(placeholder)
        }
    }

    private fun triggerStageEffects(effects: Chain, player: Player?) {
        val trigger = TriggerData(
            player = player,
            victim = entity,
            location = entity.location
        ).dispatch(entity.toDispatcher())

        addMobPlaceholders(trigger)
        effects.trigger(trigger)
    }

    override fun kill(player: Player?, removeTracking: Boolean) {
        handleRemove(removeTracking = removeTracking)

        if (!hasBeenKilled) {
            mob.spawnDrops(entity.location, player)
        }

        hasBeenKilled = true
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
