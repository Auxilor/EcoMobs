package com.willfp.ecomobs.integrations.coreprotect

import com.willfp.eco.core.integrations.Integration
import com.willfp.ecomobs.event.EcoMobSpawnerBreakEvent
import com.willfp.ecomobs.event.EcoMobSpawnerExplodeEvent
import com.willfp.ecomobs.event.EcoMobSpawnerPlaceEvent
import com.willfp.ecomobs.event.EcoMobSpawnerStackEvent
import com.willfp.ecomobs.event.EcoMobSpawnerUnstackEvent
import com.willfp.ecomobs.plugin
import com.willfp.ecomobs.spawner.SpawnerStackSettings
import com.willfp.ecomobs.spawner.spawner
import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * Logs custom spawner changes to CoreProtect.
 *
 * CoreProtect has no notion of a stacked block: a row is always one block. A stack of
 * eight is therefore logged as eight rows, so that a lookup shows the real count and
 * rolling the whole thing back leaves nothing standing.
 *
 * CoreProtect logs the block change itself for a place, a full break and an explosion,
 * so only the rest of the stack is logged here. Stacking and unstacking change no block
 * at all - unstacking cancels the break - so those are logged in full.
 */
object IntegrationCoreProtect : Listener, Integration {
    /**
     * Logged for a spawner that goes up in an explosion, where there is no player.
     * CoreProtect shows a name starting with # as a non-player source.
     */
    private const val NON_PLAYER_USER = "#ecomobs"

    /**
     * Only ever used to tell CoreProtect what the block was. A spawner has no block
     * state worth recording, so one instance serves every row.
     */
    private val spawnerData: BlockData by lazy { Material.SPAWNER.createBlockData() }

    private val coreProtect: CoreProtect? by lazy {
        Bukkit.getPluginManager().getPlugin("CoreProtect") as? CoreProtect
    }

    /**
     * Null when CoreProtect's database failed to come up, in which case it drops
     * everything logged to it anyway.
     */
    private val api: CoreProtectAPI?
        get() = coreProtect?.api?.takeIf { it.isEnabled }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handlePlace(event: EcoMobSpawnerPlaceEvent) {
        val rest = event.stackSize - 1
        if (rest <= 0) return

        val user = event.player.name
        val location = event.location

        // A later listener can still cancel the block placement and stack the spawner
        // onto the one it was placed against, so the world gets the final say.
        afterTick(location) {
            if (!isCustomSpawner(location)) {
                return@afterTick
            }

            logPlacements(user, location, rest)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleBreak(event: EcoMobSpawnerBreakEvent) {
        val rest = event.stackSize - 1
        if (rest <= 0) return

        val user = event.player.name
        val location = event.location

        // Another plugin can still cancel the block break, leaving the stack standing.
        afterTick(location) {
            if (isCustomSpawner(location)) {
                return@afterTick
            }

            logRemovals(user, location, rest)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleStack(event: EcoMobSpawnerStackEvent) {
        // Mirrors the cap SpawnerStacks applies after the event, as a listener is free
        // to raise the amount past what fits.
        val space = SpawnerStackSettings.maxSize - event.currentSize
        val taken = event.amount.coerceIn(0, space.coerceAtLeast(0))

        if (taken <= 0) return

        logPlacements(event.player?.name ?: NON_PLAYER_USER, event.location, taken)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleUnstack(event: EcoMobSpawnerUnstackEvent) {
        if (event.currentSize <= 1) return

        // Mirrors the cap SpawnerHandler applies after the event: the block stays put
        // on this path, so the stack always keeps at least one.
        val taken = event.amount.coerceIn(1, event.currentSize - 1)

        logRemovals(event.player.name, event.location, taken)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleExplode(event: EcoMobSpawnerExplodeEvent) {
        if (event.isProtected) return

        val rest = event.stackSize - 1
        if (rest <= 0) return

        val location = event.location

        // The explosion itself has not happened yet, and can still be cancelled.
        afterTick(location) {
            if (isCustomSpawner(location)) {
                return@afterTick
            }

            logRemovals(NON_PLAYER_USER, location, rest)
        }
    }

    private fun logPlacements(user: String, location: Location, amount: Int) {
        val api = api ?: return

        repeat(amount) {
            api.logPlacement(user, location, Material.SPAWNER, spawnerData)
        }
    }

    private fun logRemovals(user: String, location: Location, amount: Int) {
        val api = api ?: return

        repeat(amount) {
            api.logRemoval(user, location, Material.SPAWNER, spawnerData)
        }
    }

    /**
     * Runs [block] on the next tick, on the region owning [location].
     */
    private inline fun afterTick(location: Location, crossinline block: () -> Unit) {
        plugin.scheduler.at(location).runLater(1) { block() }
    }

    private fun isCustomSpawner(location: Location): Boolean {
        val state = location.block.state as? CreatureSpawner ?: return false
        return state.spawner.isCustomSpawner
    }

    override fun getPluginName(): String {
        return "CoreProtect"
    }
}
