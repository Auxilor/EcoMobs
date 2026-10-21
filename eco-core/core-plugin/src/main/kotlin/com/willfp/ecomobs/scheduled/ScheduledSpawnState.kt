package com.willfp.ecomobs.scheduled

import com.willfp.ecomobs.plugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

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
) {
    /**
     * If the point has room below [maxAlive] and its next spawn is due.
     */
    fun canSpawn(maxAlive: Int): Boolean =
        mobs.size < maxAlive && System.currentTimeMillis() >= nextDue
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
 * Every scheduled point's state, kept across reloads and saved to disk shortly after each change.
 *
 * Points without an entry have no mobs and are due now.
 */
object ScheduledSpawnState {
    private val file
        get() = plugin.dataFolder.resolve("data/scheduled-spawns.yml")

    private val states = ConcurrentHashMap<String, PointState>().apply { putAll(read()) }

    private val savePending = AtomicBoolean()

    operator fun get(key: String): PointState = states[key] ?: PointState()

    /**
     * Adds [uuid] if the point has fewer than [maxAlive] mobs; sets nextDue. Returns whether it was added.
     */
    fun addIfRoom(key: String, uuid: UUID, position: MobPosition, maxAlive: Int, nextDue: Long): Boolean {
        var added = false

        update(key) { current ->
            if (current.mobs.size >= maxAlive) {
                current
            } else {
                added = true
                current.copy(mobs = current.mobs + (uuid to position), nextDue = nextDue)
            }
        }

        return added
    }

    /**
     * Removes [uuid]; nextDue becomes max(nextDue, [nextDue]).
     */
    fun free(key: String, uuid: UUID, nextDue: Long) = update(key) { current ->
        if (uuid !in current.mobs) {
            current
        } else {
            current.copy(mobs = current.mobs - uuid, nextDue = maxOf(current.nextDue, nextDue))
        }
    }

    /**
     * Records that the mob [uuid] of [key] is now at [position].
     */
    fun moved(key: String, uuid: UUID, position: MobPosition) = update(key) { current ->
        if (uuid !in current.mobs) {
            current
        } else {
            current.copy(mobs = current.mobs + (uuid to position))
        }
    }

    /**
     * Sets nextDue (used after a cancelled or failed spawn).
     */
    fun delay(key: String, nextDue: Long) = update(key) { it.copy(nextDue = nextDue) }

    /**
     * Called when a tagged mob loads. Returns whether to keep it. Retired: drop from retired, remove.
     * Tracked above maxAlive: drop from mobs, remove. Tracked, or room: record position, keep. Full: remove.
     */
    fun onLoad(key: String, uuid: UUID, position: MobPosition, maxAlive: Int): Boolean {
        var keep = false

        update(key) { current ->
            when {
                uuid in current.retired -> current.copy(retired = current.retired - uuid)

                uuid in current.mobs && current.mobs.size > maxAlive -> current.copy(mobs = current.mobs - uuid)

                uuid in current.mobs || current.mobs.size < maxAlive -> {
                    keep = true
                    current.copy(mobs = current.mobs + (uuid to position))
                }

                else -> current
            }
        }

        return keep
    }

    /**
     * Moves every tracked mob to retired and sets nextDue to 0. Returns the mobs it retired.
     */
    fun reset(key: String): Set<UUID> {
        var retired = emptySet<UUID>()

        update(key) { current ->
            retired = current.mobs.keys
            PointState(retired = current.retired + current.mobs.keys)
        }

        return retired
    }

    /**
     * Drops [uuid] from retired, once its entity has been removed.
     */
    fun forgetRetired(key: String, uuid: UUID) = update(key) { it.copy(retired = it.retired - uuid) }

    /**
     * Drops state for keys not in [keys] unless the state has mobs or retired entries.
     */
    fun retain(keys: Set<String>) {
        for (key in states.keys.toList()) {
            if (key in keys) {
                continue
            }

            update(key) { current ->
                current.takeIf { it.mobs.isNotEmpty() || it.retired.isNotEmpty() }
            }
        }
    }

    fun tracked(): Map<String, Map<UUID, MobPosition>> =
        states.filterValues { it.mobs.isNotEmpty() }.mapValues { it.value.mobs }

    private inline fun update(key: String, crossinline transform: (PointState) -> PointState?) {
        var changed = false

        states.compute(key) { _, state ->
            cleaned(transform(state ?: PointState())).also { changed = it != state }
        }

        if (changed) {
            saveAsync()
        }
    }

    private fun cleaned(state: PointState?): PointState? =
        state?.takeUnless { it.mobs.isEmpty() && it.retired.isEmpty() && it.nextDue == 0L }

    private fun saveAsync() {
        if (!plugin.isEnabled || !savePending.compareAndSet(false, true)) {
            return
        }

        plugin.scheduler.async().run { save() }
    }

    @Synchronized
    fun save() {
        savePending.set(false)

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
