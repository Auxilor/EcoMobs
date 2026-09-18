package com.willfp.ecomobs.mob.impl

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.event.EcoMobDespawnEvent
import com.willfp.ecomobs.event.EcoMobStageChangeEvent
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.placeholder.MobPlaceholders
import com.willfp.ecomobs.mob.placeholder.formatMobPlaceholders
import com.willfp.ecomobs.mob.stage.DamageStage
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
import org.bukkit.persistence.PersistentDataType

private val tickKey = namespacedKeyOf("ecomobs", "tick")
private val stageIndexKey = namespacedKeyOf("ecomobs", "stage_index")
private val stageRemainingKey = namespacedKeyOf("ecomobs", "stage_remaining")

internal class LivingMobImpl(
    override val mob: EcoMob,
    override val entity: Mob,
    private val trackingRemovalCallback: () -> Unit
) : LivingMob {
    // The flags below are written from the entity's ticker and read from elsewhere: the
    // onRetired callback, chunk unload, and placeholder reads on other regions. Volatile
    // so none of those can see a stale value.
    @Volatile
    private var ticker: EcoTask? = null

    @Volatile
    private var isRunning = false

    @Volatile
    private var tick = 0

    // Set when the entity's chunk unloads, so the removal that follows isn't treated as a despawn.
    @Volatile
    private var isUnloaded = false

    // Set once the removal handlers have run, so a retire racing a cancel can't fire them twice.
    @Volatile
    private var hasHandledRemove = false

    private val tickHandlers = mutableListOf<TickHandler>()

    override val isAlive: Boolean
        get() = entity.isValid

    override val displayName: String
        get() = mob.rawDisplayName.formatMobPlaceholders(this).formatEco()

    override val ticksLeft: Int
        get() = mob.lifespan - tick

    internal val stageTracker = if (mob.usesDamageStages) {
        DamageStageTracker(mob.damageStages, ::triggerStageEffects, ::fireStageChange)
    } else {
        null
    }

    override val damageStage: DamageStage?
        get() = stageTracker?.stage

    override val damageStageNumber: Int
        get() = stageTracker?.stageNumber ?: 0

    override val damageStageProgress: Double
        get() = stageTracker?.stageProgress ?: 1.0

    override val stageRemaining: Double
        get() = stageTracker?.remaining ?: 0.0

    // Fix for drops being sent twice
    @Volatile
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
        ticker = plugin.scheduler.on(entity)
            .onRetired {
                if (!isUnloaded) {
                    handleRemove()
                }
            }
            .runTimer({ task ->
                // Dead also covers unloaded, which is handled by ChunkHandler before this runs.
                if (entity.isDead) {
                    task.cancel()
                    handleRemove()
                    return@runTimer
                }

                // A freshly loaded entity may not be valid until the server starts tracking it.
                if (!isAlive) {
                    return@runTimer
                }

                tick(tick)
                tick++

                if (tick % 20 == 0) {
                    saveState()
                }
            }, 1, 1)
    }

    /**
     * Load state saved by a previous instance of this mob, e.g. before its chunk unloaded.
     */
    fun loadState() {
        val pdc = entity.persistentDataContainer

        tick = pdc.get(tickKey, PersistentDataType.INTEGER) ?: 0

        val stageIndex = pdc.get(stageIndexKey, PersistentDataType.INTEGER) ?: return
        val stageRemaining = pdc.get(stageRemainingKey, PersistentDataType.DOUBLE) ?: return
        stageTracker?.restore(stageIndex, stageRemaining)
    }

    private fun saveState() {
        val pdc = entity.persistentDataContainer

        pdc.set(tickKey, PersistentDataType.INTEGER, tick)

        if (stageTracker != null) {
            pdc.set(stageIndexKey, PersistentDataType.INTEGER, stageTracker.index)
            pdc.set(stageRemainingKey, PersistentDataType.DOUBLE, stageTracker.remaining)
        }
    }

    /**
     * Stop tracking the mob as its chunk unloads. It is restored when the chunk loads again.
     */
    fun unload() {
        saveState()
        isUnloaded = true
        handleRemove()
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

    private fun fireStageChange(
        previousStage: DamageStage,
        stage: DamageStage?,
        stageNumber: Int,
        player: Player?
    ) {
        Bukkit.getPluginManager().callEvent(
            EcoMobStageChangeEvent(this, previousStage, stage, stageNumber, player)
        )
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
        ticker?.cancel()

        // Untracking stays outside the guard: kill(removeTracking = false) leaves the
        // mob tracked on purpose, and a later despawn must still be able to drop it.
        if (removeTracking) {
            trackingRemovalCallback()
        }

        // On Folia the entity's task can retire on one thread while another cancels it,
        // so the handlers below can be reached twice for one removal.
        if (hasHandledRemove) {
            return
        }

        hasHandledRemove = true

        plugin.topDamagerHandler.forget(entity.uniqueId)

        for (handler in this.tickHandlers) {
            handler.onRemove(this, tick)
        }
    }
}
