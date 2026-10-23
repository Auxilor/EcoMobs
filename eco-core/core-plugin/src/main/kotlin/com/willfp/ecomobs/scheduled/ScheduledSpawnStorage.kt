package com.willfp.ecomobs.scheduled

import com.willfp.ecomobs.plugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Reads and writes [ScheduledSpawnState] to disk.
 */
internal object ScheduledSpawnStorage {
    private val file
        get() = plugin.dataFolder.resolve("data/scheduled-spawns.yml")

    private val savePending = AtomicBoolean()

    private val saveLock = ReentrantLock()

    fun saveAsync() {
        if (!plugin.isEnabled || !savePending.compareAndSet(false, true)) {
            return
        }

        plugin.scheduler.async().run { save() }
    }

    fun save() {
        saveLock.withLock {
            savePending.set(false)

            val yaml = YamlConfiguration()

            for ((key, state) in ScheduledSpawnState.states) {
                yaml["$key.next-due"] = state.nextDue

                for ((uuid, position) in state.mobs) {
                    yaml["$key.mobs.$uuid.world"] = position.world
                    yaml["$key.mobs.$uuid.chunk-x"] = position.chunkX
                    yaml["$key.mobs.$uuid.chunk-z"] = position.chunkZ
                }

                if (state.retired.isNotEmpty()) {
                    yaml["$key.retired"] = state.retired.map { it.toString() }
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
            } catch (ignored: AtomicMoveNotSupportedException) {
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    fun read(): Map<String, PointState> {
        if (!file.exists()) {
            return emptyMap()
        }

        val yaml = YamlConfiguration.loadConfiguration(file)

        return yaml.getKeys(false).associateWith { key ->
            PointState(
                mobs = yaml.getConfigurationSection("$key.mobs")?.let { section ->
                    section.getKeys(false).mapNotNull { id -> readMob(section, id) }.toMap()
                }.orEmpty(),
                retired = yaml.getStringList("$key.retired").mapNotNull {
                    runCatching { UUID.fromString(it) }.getOrNull()
                }.toSet(),
                nextDue = yaml.getLong("$key.next-due")
            )
        }
    }

    private fun readMob(section: ConfigurationSection, id: String): Pair<UUID, MobPosition>? {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull()
        val world = section.getString("$id.world")

        return if (uuid != null && world != null) {
            uuid to MobPosition(
                world = world,
                chunkX = section.getInt("$id.chunk-x"),
                chunkZ = section.getInt("$id.chunk-z")
            )
        } else {
            null
        }
    }
}
