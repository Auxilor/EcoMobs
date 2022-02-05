package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.ecobosses.tick.BossTicker
import com.willfp.ecobosses.tick.DisplayNameTicker
import com.willfp.ecobosses.tick.LifespanTicker
import com.willfp.ecobosses.tick.TargetTicker
import com.willfp.ecobosses.util.ConfiguredSound
import com.willfp.ecobosses.util.PlayableSound
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID

class EcoBoss(
    val config: Config,
    private val plugin: EcoPlugin
) : Holder {
    val id: String = config.getString("id")

    val displayName: String = config.getString("displayName")

    val lifespan = config.getInt("lifespan")

    val influenceRadius = config.getDouble("influenceRadius")

    val targetRange = config.getDouble("target.range")

    val targetMode = TargetMode.getByID(config.getString("target.mode"))!!

    private val sounds: Map<BossLifecycle, PlayableSound> = run {
        val map = mutableMapOf<BossLifecycle, PlayableSound>()

        for (value in BossLifecycle.values()) {
            map[value] = PlayableSound(config.getSubsections("sounds.${value.name.lowercase()}").map {
                ConfiguredSound.fromConfig(it)
            })
        }

        map
    }

    private val mob: TestableEntity = Entities.lookup(config.getString("mob"))

    private val currentlyAlive = mutableMapOf<UUID, LivingEcoBoss>()

    override val conditions = emptySet<ConfiguredCondition>()

    override val effects = config.getSubsections("effects").mapNotNull {
        Effects.compile(it, "Boss ID $id")
    }.toSet()

    fun markDead(uuid: UUID) {
        currentlyAlive.remove(uuid)
    }

    operator fun get(uuid: UUID): LivingEcoBoss? {
        return currentlyAlive[uuid]
    }

    operator fun get(entity: LivingEntity): LivingEcoBoss? {
        return currentlyAlive[entity.uniqueId]
    }

    fun getAllAlive(): Set<LivingEntity> {
        return currentlyAlive.values.mapNotNull { it.entity }.toSet()
    }

    fun spawn(location: Location) {
        val mob = mob.spawn(location) as LivingEntity
        currentlyAlive[mob.uniqueId] = LivingEcoBoss(
            plugin,
            mob.uniqueId,
            this,
            createTickersFor(mob)
        )
    }

    private fun createTickersFor(entity: LivingEntity): Set<BossTicker> {
        return setOf(
            LifespanTicker(),
            DisplayNameTicker(),
            TargetTicker()
        )
    }

    fun handleLifecycle(player: Player, lifecycle: BossLifecycle) {
        sounds[lifecycle]?.play(player)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoBoss) {
            return false
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return ("EcoBoss{"
                + id
                + "}")
    }
}

class SimpleHolder(
    override val conditions: Set<ConfiguredCondition>,
    override val effects: Set<ConfiguredEffect>
) : Holder