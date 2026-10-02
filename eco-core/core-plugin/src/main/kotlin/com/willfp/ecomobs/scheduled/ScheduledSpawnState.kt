package com.willfp.ecomobs.scheduled

import com.willfp.ecomobs.plugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The chunk a scheduled mob was last known to be in.
 */
data class MobPosition(
    val world: String,
    val chunkX: Int,
    val chunkZ: Int
)

/**
 * The mobs alive from a point, the mobs retired from it, and when it may next spawn.
 */
data class PointState(
    val mobs: Map<UUID, MobPosition> = emptyMap(),
    val retired: Set<UUID> = emptySet(),
    val nextDue: Long = 0L
)

enum class LoadOutcome {
    TRACKED,
    ADOPTED,
    REMOVE
}

/**
 * The chunk position of [this] location.
 */
fun Location.toMobPosition(): MobPosition = MobPosition(
    world?.name.orEmpty(),
    blockX shr 4,
    blockZ shr 4
)

/**
 * Every scheduled point's state, kept across reloads and saved to disk on each change.
 *
 * Points without an entry have no mobs and are due now.
 */
object ScheduledSpawnState {
    private val file
        get() = plugin.dataFolder.resolve("data/scheduled-spawns.yml")

    private val states = ConcurrentHashMap<String, PointState>().apply { putAll(read()) }

    operator fun get(key: String): PointState = states[key] ?: PointState()

    /**
     * Adds [uuid] if the point has fewer than [maxAlive] mobs; sets nextDue. Returns whether it was added.
     */
    fun addIfRoom(key: String, uuid: UUID, position: MobPosition, maxAlive: Int, nextDue: Long): Boolean {
        var added = false

        states.compute(key) { _, state ->
            val current = state ?: PointState()

            if (current.mobs.size >= maxAlive) {
                return@compute state
            }

            added = true
            current.copy(mobs = current.mobs + (uuid to position), nextDue = nextDue)
        }

        if (added) {
            saveAsync()
        }

        return added
    }

    /**
     * Removes [uuid]; nextDue becomes max(nextDue, [nextDue]). Returns whether it was tracked.
     */
    fun free(key: String, uuid: UUID, nextDue: Long): Boolean {
        var freed = false

        states.computeIfPresent(key) { _, state ->
            if (uuid !in state.mobs) {
                return@computeIfPresent state
            }

            freed = true
            cleaned(state.copy(mobs = state.mobs - uuid, nextDue = maxOf(state.nextDue, nextDue)))
        }

        if (freed) {
            saveAsync()
        }

        return freed
    }

    /**
     * Records that the mob [uuid] of [key] is now at [position].
     */
    fun moved(key: String, uuid: UUID, position: MobPosition) {
        var changed = false

        states.computeIfPresent(key) { _, state ->
            val current = state.mobs[uuid]

            if (current == null || current == position) {
                return@computeIfPresent state
            }

            changed = true
            state.copy(mobs = state.mobs + (uuid to position))
        }

        if (changed) {
            saveAsync()
        }
    }

    /**
     * Sets nextDue (used after a cancelled spawn).
     */
    fun delay(key: String, nextDue: Long) {
        states.compute(key) { _, state ->
            (state ?: PointState()).copy(nextDue = nextDue)
        }

        saveAsync()
    }

    /**
     * Called when a tagged mob loads. Retired: drop from retired, REMOVE. Tracked: update position,
     * TRACKED. Room: add, ADOPTED. Full: REMOVE.
     */
    fun onLoad(key: String, uuid: UUID, position: MobPosition, maxAlive: Int): LoadOutcome {
        var outcome = LoadOutcome.REMOVE
        var changed = false

        states.compute(key) { _, state ->
            val current = state ?: PointState()

            when {
                uuid in current.retired -> {
                    changed = true
                    cleaned(current.copy(retired = current.retired - uuid))
                }

                uuid in current.mobs -> {
                    outcome = LoadOutcome.TRACKED
                    changed = current.mobs[uuid] != position
                    current.copy(mobs = current.mobs + (uuid to position))
                }

                current.mobs.size < maxAlive -> {
                    outcome = LoadOutcome.ADOPTED
                    changed = true
                    current.copy(mobs = current.mobs + (uuid to position))
                }

                else -> state
            }
        }

        if (changed) {
            saveAsync()
        }

        return outcome
    }

    /**
     * Moves every tracked mob not in [loaded] to retired, drops [loaded] from mobs and sets nextDue to 0.
     */
    fun reset(key: String, loaded: Set<UUID>) {
        states.compute(key) { _, state ->
            val current = state ?: return@compute null

            cleaned(PointState(retired = current.retired + current.mobs.keys.filter { it !in loaded }))
        }

        saveAsync()
    }

    /**
     * Drops state for keys not in [keys] unless the state has mobs or retired entries.
     */
    fun retain(keys: Set<String>) {
        var changed = false

        for (key in states.keys.toList()) {
            if (key in keys) {
                continue
            }

            states.computeIfPresent(key) { _, state ->
                if (state.mobs.isNotEmpty() || state.retired.isNotEmpty()) {
                    return@computeIfPresent state
                }

                changed = true
                null
            }
        }

        if (changed) {
            saveAsync()
        }
    }

    fun tracked(): Map<String, Map<UUID, MobPosition>> =
        states.filterValues { it.mobs.isNotEmpty() }.mapValues { it.value.mobs }

    private fun cleaned(state: PointState): PointState? =
        state.takeUnless { it.mobs.isEmpty() && it.retired.isEmpty() && it.nextDue == 0L }

    private fun saveAsync() {
        if (!plugin.isEnabled) {
            return
        }

        plugin.scheduler.async().run { save() }
    }

    @Synchronized
    fun save() {
        val yaml = YamlConfiguration()

        for ((key, state) in states) {
            yaml.set("$key.next-due", state.nextDue)

            for ((uuid, position) in state.mobs) {
                yaml.set("$key.mobs.$uuid.world", position.world)
                yaml.set("$key.mobs.$uuid.chunk-x", position.chunkX)
                yaml.set("$key.mobs.$uuid.chunk-z", position.chunkZ)
            }

            if (state.retired.isNotEmpty()) {
                yaml.set("$key.retired", state.retired.map { it.toString() })
            }
        }

        file.parentFile.mkdirs()

        val tempFile = file.resolveSibling("${file.name}.tmp")
        yaml.save(tempFile)

        try {
            Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (exception: AtomicMoveNotSupportedException) {
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun read(): Map<String, PointState> {
        if (!file.exists()) {
            return emptyMap()
        }

        val yaml = YamlConfiguration.loadConfiguration(file)

        return yaml.getKeys(false).associateWith { key ->
            PointState(
                mobs = yaml.getConfigurationSection("$key.mobs")?.let { section ->
                    section.getKeys(false).mapNotNull { id ->
                        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return@mapNotNull null
                        val world = section.getString("$id.world") ?: return@mapNotNull null

                        uuid to MobPosition(world, section.getInt("$id.chunk-x"), section.getInt("$id.chunk-z"))
                    }.toMap()
                }.orEmpty(),
                retired = yaml.getStringList("$key.retired").mapNotNull {
                    runCatching { UUID.fromString(it) }.getOrNull()
                }.toSet(),
                nextDue = yaml.getLong("$key.next-due")
            )
        }
    }
}
