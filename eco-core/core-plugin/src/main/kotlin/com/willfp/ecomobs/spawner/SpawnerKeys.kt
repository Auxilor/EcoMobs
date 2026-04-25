// spawner/SpawnerKeys.kt
package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

val spawnerMobKey = namespacedKeyOf("ecomobs", "spawner_mob")
val spawnerDelayMinKey = namespacedKeyOf("ecomobs", "spawner_delay_min")
val spawnerDelayMaxKey = namespacedKeyOf("ecomobs", "spawner_delay_max")
val spawnerSpawnCountKey = namespacedKeyOf("ecomobs", "spawner_spawn_count")
val spawnerSpawnRangeKey = namespacedKeyOf("ecomobs", "spawner_spawn_range")
val spawnerPlayerRangeKey = namespacedKeyOf("ecomobs", "spawner_player_range")
val spawnerMaxNearbyKey = namespacedKeyOf("ecomobs", "spawner_max_nearby")
val spawnerPickupKey = namespacedKeyOf("ecomobs", "spawner_pickup")
val spawnerParticleAnimKey = namespacedKeyOf("ecomobs", "spawner_particle_anim")

// ── FastItemStack extensions ──────────────────────────────────────────────────

val FastItemStack.isCustomSpawner: Boolean
    get() = persistentDataContainer.has(spawnerMobKey, PersistentDataType.STRING)

var FastItemStack.spawnerMob: String?
    get() = persistentDataContainer.get(spawnerMobKey, PersistentDataType.STRING)
    set(value) {
        if (value == null) persistentDataContainer.remove(spawnerMobKey)
        else persistentDataContainer.set(spawnerMobKey, PersistentDataType.STRING, value)
    }

var FastItemStack.spawnerDelayMin: Int
    get() = persistentDataContainer.get(spawnerDelayMinKey, PersistentDataType.INTEGER) ?: 200
    set(value) {
        persistentDataContainer.set(spawnerDelayMinKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerDelayMax: Int
    get() = persistentDataContainer.get(spawnerDelayMaxKey, PersistentDataType.INTEGER) ?: 800
    set(value) {
        persistentDataContainer.set(spawnerDelayMaxKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerSpawnCount: Int
    get() = persistentDataContainer.get(spawnerSpawnCountKey, PersistentDataType.INTEGER) ?: 4
    set(value) {
        persistentDataContainer.set(spawnerSpawnCountKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerSpawnRange: Int
    get() = persistentDataContainer.get(spawnerSpawnRangeKey, PersistentDataType.INTEGER) ?: 4
    set(value) {
        persistentDataContainer.set(spawnerSpawnRangeKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerPlayerRange: Int
    get() = persistentDataContainer.get(spawnerPlayerRangeKey, PersistentDataType.INTEGER) ?: 16
    set(value) {
        persistentDataContainer.set(spawnerPlayerRangeKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerMaxNearby: Int
    get() = persistentDataContainer.get(spawnerMaxNearbyKey, PersistentDataType.INTEGER) ?: 6
    set(value) {
        persistentDataContainer.set(spawnerMaxNearbyKey, PersistentDataType.INTEGER, value)
    }

var FastItemStack.spawnerPickup: String
    get() = persistentDataContainer.get(spawnerPickupKey, PersistentDataType.STRING) ?: "deny"
    set(value) {
        persistentDataContainer.set(spawnerPickupKey, PersistentDataType.STRING, value)
    }

var FastItemStack.spawnerParticleAnim: String?
    get() = persistentDataContainer.get(spawnerParticleAnimKey, PersistentDataType.STRING)
    set(value) {
        if (value == null) persistentDataContainer.remove(spawnerParticleAnimKey)
        else persistentDataContainer.set(spawnerParticleAnimKey, PersistentDataType.STRING, value)
    }

// ── ItemStack convenience extensions ─────────────────────────────────────────

val ItemStack.isCustomSpawner: Boolean get() = fast().isCustomSpawner
var ItemStack.spawnerMob: String?
    get() = fast().spawnerMob
    set(value) {
        fast().spawnerMob = value
    }

// ── CreatureSpawner (block state) extensions ──────────────────────────────────

val CreatureSpawner.isCustomSpawner: Boolean
    get() = persistentDataContainer.has(spawnerMobKey, PersistentDataType.STRING)

var CreatureSpawner.spawnerMob: String?
    get() = persistentDataContainer.get(spawnerMobKey, PersistentDataType.STRING)
    set(value) {
        if (value == null) persistentDataContainer.remove(spawnerMobKey)
        else persistentDataContainer.set(spawnerMobKey, PersistentDataType.STRING, value)
    }

var CreatureSpawner.spawnerDelayMin: Int
    get() = persistentDataContainer.get(spawnerDelayMinKey, PersistentDataType.INTEGER) ?: 200
    set(value) {
        persistentDataContainer.set(spawnerDelayMinKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerDelayMax: Int
    get() = persistentDataContainer.get(spawnerDelayMaxKey, PersistentDataType.INTEGER) ?: 800
    set(value) {
        persistentDataContainer.set(spawnerDelayMaxKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerSpawnCount: Int
    get() = persistentDataContainer.get(spawnerSpawnCountKey, PersistentDataType.INTEGER) ?: 4
    set(value) {
        persistentDataContainer.set(spawnerSpawnCountKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerSpawnRange: Int
    get() = persistentDataContainer.get(spawnerSpawnRangeKey, PersistentDataType.INTEGER) ?: 4
    set(value) {
        persistentDataContainer.set(spawnerSpawnRangeKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerPlayerRange: Int
    get() = persistentDataContainer.get(spawnerPlayerRangeKey, PersistentDataType.INTEGER) ?: 16
    set(value) {
        persistentDataContainer.set(spawnerPlayerRangeKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerMaxNearby: Int
    get() = persistentDataContainer.get(spawnerMaxNearbyKey, PersistentDataType.INTEGER) ?: 6
    set(value) {
        persistentDataContainer.set(spawnerMaxNearbyKey, PersistentDataType.INTEGER, value)
    }

var CreatureSpawner.spawnerPickup: String
    get() = persistentDataContainer.get(spawnerPickupKey, PersistentDataType.STRING) ?: "deny"
    set(value) {
        persistentDataContainer.set(spawnerPickupKey, PersistentDataType.STRING, value)
    }

var CreatureSpawner.spawnerParticleAnim: String?
    get() = persistentDataContainer.get(spawnerParticleAnimKey, PersistentDataType.STRING)
    set(value) {
        if (value == null) persistentDataContainer.remove(spawnerParticleAnimKey)
        else persistentDataContainer.set(spawnerParticleAnimKey, PersistentDataType.STRING, value)
    }

// ── Transfer helpers ──────────────────────────────────────────────────────────

fun FastItemStack.copySpawnerPdcTo(state: CreatureSpawner) {
    state.spawnerMob = spawnerMob
    state.spawnerDelayMin = spawnerDelayMin
    state.spawnerDelayMax = spawnerDelayMax
    state.spawnerSpawnCount = spawnerSpawnCount
    state.spawnerSpawnRange = spawnerSpawnRange
    state.spawnerPlayerRange = spawnerPlayerRange
    state.spawnerMaxNearby = spawnerMaxNearby
    state.spawnerPickup = spawnerPickup
    state.spawnerParticleAnim = spawnerParticleAnim
}

fun CreatureSpawner.applyVanillaSettings() {
    minSpawnDelay = spawnerDelayMin
    maxSpawnDelay = spawnerDelayMax
    spawnCount = spawnerSpawnCount
    spawnRange = spawnerSpawnRange
    requiredPlayerRange = spawnerPlayerRange
    maxNearbyEntities = spawnerMaxNearby
}

fun CreatureSpawner.toSpawnerItem(): ItemStack {
    val item = ItemStack(Material.SPAWNER)
    val fis = item.fast()
    fis.spawnerMob = spawnerMob
    fis.spawnerDelayMin = spawnerDelayMin
    fis.spawnerDelayMax = spawnerDelayMax
    fis.spawnerSpawnCount = spawnerSpawnCount
    fis.spawnerSpawnRange = spawnerSpawnRange
    fis.spawnerPlayerRange = spawnerPlayerRange
    fis.spawnerMaxNearby = spawnerMaxNearby
    fis.spawnerPickup = spawnerPickup
    fis.spawnerParticleAnim = spawnerParticleAnim
    return fis.unwrap()
}
