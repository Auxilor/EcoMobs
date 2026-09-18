package com.willfp.ecomobs.spawner

import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.impl.ConfigDrivenEcoMob
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

val spawnerMobKey = namespacedKeyOf("ecomobs", "spawner_mob")
val entityFromSpawnerKey = namespacedKeyOf("ecomobs", "from_spawner")
val spawnerDelayMinKey = namespacedKeyOf("ecomobs", "spawner_delay_min")
val spawnerDelayMaxKey = namespacedKeyOf("ecomobs", "spawner_delay_max")
val spawnerSpawnCountKey = namespacedKeyOf("ecomobs", "spawner_spawn_count")
val spawnerSpawnRangeKey = namespacedKeyOf("ecomobs", "spawner_spawn_range")
val spawnerPlayerRangeKey = namespacedKeyOf("ecomobs", "spawner_player_range")
val spawnerMaxNearbyKey = namespacedKeyOf("ecomobs", "spawner_max_nearby")
val spawnerPickupKey = namespacedKeyOf("ecomobs", "spawner_pickup")
val spawnerParticleAnimKey = namespacedKeyOf("ecomobs", "spawner_particle_anim")
val spawnerExplosionProofKey = namespacedKeyOf("ecomobs", "spawner_explosion_proof")
val spawnerStackSizeKey = namespacedKeyOf("ecomobs", "spawner_stack_size")
val spawnerNoAIKey = namespacedKeyOf("ecomobs", "spawner_no_ai")

/**
 * The values a spawner falls back to when its data has never been written.
 */
object SpawnerDefaults {
    const val DELAY_MIN = 200
    const val DELAY_MAX = 800
    const val SPAWN_COUNT = 4
    const val SPAWN_RANGE = 4
    const val PLAYER_RANGE = 16
    const val MAX_NEARBY = 6
    const val PICKUP = "deny"
}

private fun PersistentDataContainer.getInt(key: NamespacedKey, default: Int) =
    get(key, PersistentDataType.INTEGER) ?: default

private fun PersistentDataContainer.setInt(key: NamespacedKey, value: Int) =
    set(key, PersistentDataType.INTEGER, value)

private fun PersistentDataContainer.setOrRemove(key: NamespacedKey, value: String?) {
    if (value == null) remove(key) else set(key, PersistentDataType.STRING, value)
}

/**
 * A spawner's configuration, stored in [pdc].
 *
 * Both spawner items and placed spawner blocks carry their settings the same
 * way, so they share this view rather than each having their own accessors.
 */
@JvmInline
value class SpawnerData(val pdc: PersistentDataContainer) {
    val isCustomSpawner: Boolean
        get() = pdc.has(spawnerMobKey, PersistentDataType.STRING)

    var mob: String?
        get() = pdc.get(spawnerMobKey, PersistentDataType.STRING)
        set(value) = pdc.setOrRemove(spawnerMobKey, value)

    var delayMin: Int
        get() = pdc.getInt(spawnerDelayMinKey, SpawnerDefaults.DELAY_MIN)
        set(value) = pdc.setInt(spawnerDelayMinKey, value)

    var delayMax: Int
        get() = pdc.getInt(spawnerDelayMaxKey, SpawnerDefaults.DELAY_MAX)
        set(value) = pdc.setInt(spawnerDelayMaxKey, value)

    var spawnCount: Int
        get() = pdc.getInt(spawnerSpawnCountKey, SpawnerDefaults.SPAWN_COUNT)
        set(value) = pdc.setInt(spawnerSpawnCountKey, value)

    var spawnRange: Int
        get() = pdc.getInt(spawnerSpawnRangeKey, SpawnerDefaults.SPAWN_RANGE)
        set(value) = pdc.setInt(spawnerSpawnRangeKey, value)

    var playerRange: Int
        get() = pdc.getInt(spawnerPlayerRangeKey, SpawnerDefaults.PLAYER_RANGE)
        set(value) = pdc.setInt(spawnerPlayerRangeKey, value)

    var maxNearby: Int
        get() = pdc.getInt(spawnerMaxNearbyKey, SpawnerDefaults.MAX_NEARBY)
        set(value) = pdc.setInt(spawnerMaxNearbyKey, value)

    var pickup: String
        get() = pdc.get(spawnerPickupKey, PersistentDataType.STRING) ?: SpawnerDefaults.PICKUP
        set(value) {
            pdc.set(spawnerPickupKey, PersistentDataType.STRING, value)
        }

    var particleAnim: String?
        get() = pdc.get(spawnerParticleAnimKey, PersistentDataType.STRING)
        set(value) = pdc.setOrRemove(spawnerParticleAnimKey, value)

    var explosionProof: Boolean
        get() = pdc.get(spawnerExplosionProofKey, PersistentDataType.BYTE) == 1.toByte()
        set(value) {
            pdc.set(spawnerExplosionProofKey, PersistentDataType.BYTE, if (value) 1 else 0)
        }

    /**
     * Whether the mobs this spawner spawns have their AI stripped.
     */
    var noAI: Boolean
        get() = pdc.get(spawnerNoAIKey, PersistentDataType.BYTE) == 1.toByte()
        set(value) {
            pdc.set(spawnerNoAIKey, PersistentDataType.BYTE, if (value) 1 else 0)
        }

    /**
     * How many spawners this one stands in for.
     */
    var stackSize: Int
        get() = pdc.getInt(spawnerStackSizeKey, 1)
        set(value) {
            // A stack of one is just a spawner, so it carries no data at all.
            if (value <= 1) {
                pdc.remove(spawnerStackSizeKey)
            } else {
                pdc.setInt(spawnerStackSizeKey, value)
            }
        }

    /**
     * Whether two spawners are identical in everything but stack size, and so can merge.
     */
    fun matches(other: SpawnerData): Boolean =
        mob == other.mob &&
                delayMin == other.delayMin &&
                delayMax == other.delayMax &&
                spawnCount == other.spawnCount &&
                spawnRange == other.spawnRange &&
                playerRange == other.playerRange &&
                maxNearby == other.maxNearby &&
                pickup == other.pickup &&
                particleAnim == other.particleAnim &&
                explosionProof == other.explosionProof &&
                noAI == other.noAI

    fun copyTo(other: SpawnerData) {
        other.mob = mob
        other.delayMin = delayMin
        other.delayMax = delayMax
        other.spawnCount = spawnCount
        other.spawnRange = spawnRange
        other.playerRange = playerRange
        other.maxNearby = maxNearby
        other.pickup = pickup
        other.particleAnim = particleAnim
        other.explosionProof = explosionProof
        other.noAI = noAI
        other.stackSize = stackSize
    }
}

val FastItemStack.spawner: SpawnerData
    get() = SpawnerData(persistentDataContainer)

val CreatureSpawner.spawner: SpawnerData
    get() = SpawnerData(persistentDataContainer)

fun CreatureSpawner.applyVanillaSettings() {
    val data = spawner

    minSpawnDelay = data.delayMin
    maxSpawnDelay = data.delayMax
    spawnCount = data.spawnCount
    spawnRange = data.spawnRange
    requiredPlayerRange = data.playerRange
    maxNearbyEntities = data.maxNearby
}

fun entityTypeOrNull(name: String): EntityType? =
    EntityType.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }

/**
 * Resolves the EntityType for a mob ID so the vanilla spawner preview spins
 * the correct entity. Handles both plain vanilla IDs and EcoMob IDs.
 */
fun resolveEntityType(mobId: String): EntityType? {
    // Plain vanilla entity type (e.g. "zombie", "ZOMBIE")
    entityTypeOrNull(mobId)?.let { return it }

    // EcoMob — fall back to the entity its base mob is built from
    val baseMobId = (EcoMobs[mobId] as? ConfigDrivenEcoMob)?.baseMobId ?: return null

    return entityTypeOrNull(baseMobId)
}

/**
 * The spawner as an item, carrying its whole stack unless [stackSize] says otherwise.
 */
fun CreatureSpawner.toSpawnerItem(stackSize: Int = spawner.stackSize): ItemStack {
    val item = ItemStack(Material.SPAWNER)
    val fis = item.fast()
    spawner.copyTo(fis.spawner)
    fis.spawner.stackSize = stackSize
    return fis.unwrap()
}
