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
val spawnerAdoptedKey = namespacedKeyOf("ecomobs", "spawner_adopted")

/**
 * The per-spawner settings that are numbers, which are the ones an adopted spawner can
 * leave unwritten to follow [VanillaSpawnerDefaults] instead.
 */
private val intKeys = listOf(
    spawnerDelayMinKey,
    spawnerDelayMaxKey,
    spawnerSpawnCountKey,
    spawnerSpawnRangeKey,
    spawnerPlayerRangeKey,
    spawnerMaxNearbyKey
)

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

    /**
     * Whether this is a vanilla spawner EcoMobs adopted, whose unwritten settings follow
     * [VanillaSpawnerDefaults] rather than [SpawnerDefaults].
     */
    var isAdopted: Boolean
        get() = pdc.get(spawnerAdoptedKey, PersistentDataType.BYTE) == 1.toByte()
        set(value) {
            if (value) pdc.set(spawnerAdoptedKey, PersistentDataType.BYTE, 1) else pdc.remove(spawnerAdoptedKey)
        }

    var mob: String?
        get() = pdc.get(spawnerMobKey, PersistentDataType.STRING)
        set(value) = pdc.setOrRemove(spawnerMobKey, value)

    var delayMin: Int
        get() = pdc.getInt(spawnerDelayMinKey, if (isAdopted) VanillaSpawnerDefaults.delayMin else SpawnerDefaults.DELAY_MIN)
        set(value) = pdc.setInt(spawnerDelayMinKey, value)

    var delayMax: Int
        get() = pdc.getInt(spawnerDelayMaxKey, if (isAdopted) VanillaSpawnerDefaults.delayMax else SpawnerDefaults.DELAY_MAX)
        set(value) = pdc.setInt(spawnerDelayMaxKey, value)

    var spawnCount: Int
        get() = pdc.getInt(spawnerSpawnCountKey, if (isAdopted) VanillaSpawnerDefaults.spawnCount else SpawnerDefaults.SPAWN_COUNT)
        set(value) = pdc.setInt(spawnerSpawnCountKey, value)

    var spawnRange: Int
        get() = pdc.getInt(spawnerSpawnRangeKey, if (isAdopted) VanillaSpawnerDefaults.spawnRange else SpawnerDefaults.SPAWN_RANGE)
        set(value) = pdc.setInt(spawnerSpawnRangeKey, value)

    var playerRange: Int
        get() = pdc.getInt(spawnerPlayerRangeKey, if (isAdopted) VanillaSpawnerDefaults.playerRange else SpawnerDefaults.PLAYER_RANGE)
        set(value) = pdc.setInt(spawnerPlayerRangeKey, value)

    var maxNearby: Int
        get() = pdc.getInt(spawnerMaxNearbyKey, if (isAdopted) VanillaSpawnerDefaults.maxNearby else SpawnerDefaults.MAX_NEARBY)
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
        other.isAdopted = isAdopted

        // Only what was written is copied, so a replica of an adopted spawner carries on
        // following the vanilla defaults rather than freezing them at the time of copying.
        for (key in intKeys) {
            val value = pdc.get(key, PersistentDataType.INTEGER)
            if (value == null) other.pdc.remove(key) else other.pdc.setInt(key, value)
        }

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

/**
 * A spawner's settings as the spawn loop applies them: EcoMobs' own data where it has
 * been written, and the block's own values otherwise.
 *
 * EcoMobs ticks every spawner, including the ones it never placed - dungeon spawners,
 * `/setblock`, world edits - and those carry their settings in the block state alone,
 * so they have to tick as themselves rather than as the EcoMobs defaults.
 */
val CreatureSpawner.effectiveMob: String?
    get() = spawner.mob ?: spawnedType?.name?.lowercase()

val CreatureSpawner.effectiveDelayMin: Int
    get() = if (spawner.isCustomSpawner) spawner.delayMin else
        orVanillaDefault(minSpawnDelay, VanillaSpawnerDefaults.Stock.DELAY_MIN, VanillaSpawnerDefaults.delayMin)

val CreatureSpawner.effectiveDelayMax: Int
    get() = if (spawner.isCustomSpawner) spawner.delayMax else
        orVanillaDefault(maxSpawnDelay, VanillaSpawnerDefaults.Stock.DELAY_MAX, VanillaSpawnerDefaults.delayMax)

val CreatureSpawner.effectiveSpawnCount: Int
    get() = if (spawner.isCustomSpawner) spawner.spawnCount else
        orVanillaDefault(spawnCount, VanillaSpawnerDefaults.Stock.SPAWN_COUNT, VanillaSpawnerDefaults.spawnCount)

val CreatureSpawner.effectiveSpawnRange: Int
    get() = if (spawner.isCustomSpawner) spawner.spawnRange else
        orVanillaDefault(spawnRange, VanillaSpawnerDefaults.Stock.SPAWN_RANGE, VanillaSpawnerDefaults.spawnRange)

val CreatureSpawner.effectivePlayerRange: Int
    get() = if (spawner.isCustomSpawner) spawner.playerRange else
        orVanillaDefault(requiredPlayerRange, VanillaSpawnerDefaults.Stock.PLAYER_RANGE, VanillaSpawnerDefaults.playerRange)

val CreatureSpawner.effectiveMaxNearby: Int
    get() = if (spawner.isCustomSpawner) spawner.maxNearby else
        orVanillaDefault(maxNearbyEntities, VanillaSpawnerDefaults.Stock.MAX_NEARBY, VanillaSpawnerDefaults.maxNearby)

/**
 * A block's own spawner value, or the configured vanilla default when the block still
 * has what the server gives every spawner. A value someone changed is theirs to keep.
 */
private fun orVanillaDefault(blockValue: Int, stock: Int, configured: Int): Int =
    if (blockValue == stock) configured else blockValue

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
 * The spawner as it is tracked in [PlacedSpawners], taken from the block state so the
 * index carries the same data the block does.
 */
fun CreatureSpawner.toPlacedSpawner(): PlacedSpawner =
    PlacedSpawner(location, spawner.particleAnim, effectiveMob, spawner.stackSize)

/**
 * Writes this spawner's own settings into EcoMobs' data, for a spawner that has never
 * carried any - a dungeon spawner, a `/setblock`, a world edit.
 *
 * Ticking never needed this, as the loop reads whichever of the two a spawner has. What
 * does need it is everything that only EcoMobs spawners can do: stacking, holograms,
 * pickup rules, the attribute commands. All of those live in the data, so a spawner has
 * to have some.
 *
 * Does nothing to a spawner that already has data, or to one with no mob to copy.
 * Returns whether anything was written.
 */
fun CreatureSpawner.adoptVanillaSettings(): Boolean {
    if (spawner.isCustomSpawner) {
        return false
    }

    return copyVanillaSettingsInto(spawner)
}

/**
 * Marks [data] as an adopted copy of this block's spawner. False when there is no mob
 * to copy.
 *
 * Values the block still has at the server's stock are left unwritten, so they follow
 * `spawners.vanilla-spawners` and pick up any change to it on reload. Only the values
 * someone changed on the block - a map maker's custom NBT - are written, and kept.
 */
private fun CreatureSpawner.copyVanillaSettingsInto(data: SpawnerData): Boolean {
    data.mob = effectiveMob ?: return false
    data.isAdopted = true

    val stock = VanillaSpawnerDefaults.Stock

    if (minSpawnDelay != stock.DELAY_MIN) data.delayMin = minSpawnDelay
    if (maxSpawnDelay != stock.DELAY_MAX) data.delayMax = maxSpawnDelay
    if (spawnCount != stock.SPAWN_COUNT) data.spawnCount = spawnCount
    if (spawnRange != stock.SPAWN_RANGE) data.spawnRange = spawnRange
    if (requiredPlayerRange != stock.PLAYER_RANGE) data.playerRange = requiredPlayerRange
    if (maxNearbyEntities != stock.MAX_NEARBY) data.maxNearby = maxNearbyEntities

    return true
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

/**
 * The spawner as an EcoMobs spawner item, whichever kind of spawner it is.
 *
 * A spawner with no EcoMobs data - a dungeon spawner - would otherwise give back the
 * plain block, which carries no mob and so stacks with nothing and places as an empty
 * spawner. This copies its own settings into the item instead, so what you get back is
 * the spawner you were looking at.
 */
fun CreatureSpawner.toReplicaSpawnerItem(): ItemStack {
    if (spawner.isCustomSpawner) {
        return toSpawnerItem()
    }

    val item = ItemStack(Material.SPAWNER)
    val fis = item.fast()

    // A spawner with nothing in it gives back the plain block, as there is nothing to
    // make a replica of.
    if (!copyVanillaSettingsInto(fis.spawner)) {
        return item
    }

    return fis.unwrap()
}
