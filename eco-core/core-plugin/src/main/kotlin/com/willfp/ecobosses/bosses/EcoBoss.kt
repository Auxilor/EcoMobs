package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.toComponent
import com.willfp.ecobosses.tick.BossBarTicker
import com.willfp.ecobosses.tick.BossTicker
import com.willfp.ecobosses.tick.DisplayNameTicker
import com.willfp.ecobosses.tick.LifespanTicker
import com.willfp.ecobosses.tick.TargetTicker
import com.willfp.ecobosses.util.BossDrop
import com.willfp.ecobosses.util.CommandReward
import com.willfp.ecobosses.util.ConfiguredSound
import com.willfp.ecobosses.util.LocalBroadcast
import com.willfp.ecobosses.util.PlayableSound
import com.willfp.ecobosses.util.XpReward
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.Effects
import net.kyori.adventure.bossbar.BossBar
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

    val isBossBarEnabled = config.getBool("bossBar.enabled")

    val bossBarRadius = config.getDouble("bossBar.radius")

    private val bossBarColor = BossBar.Color.valueOf(config.getString("bossBar.color").uppercase())

    private val bossBarStyle = BossBar.Overlay.valueOf(config.getString("bossBar.style").uppercase())

    private val sounds: Map<BossLifecycle, PlayableSound> = run {
        val map = mutableMapOf<BossLifecycle, PlayableSound>()

        for (value in BossLifecycle.values()) {
            map[value] = PlayableSound(config.getSubsections("sounds.${value.name.lowercase()}").map {
                ConfiguredSound.fromConfig(it)
            })
        }

        map
    }

    private val messages: Map<BossLifecycle, Iterable<LocalBroadcast>> = run {
        val map = mutableMapOf<BossLifecycle, Iterable<LocalBroadcast>>()

        for (value in BossLifecycle.values()) {
            map[value] = config.getSubsections("messages.${value.name.lowercase()}").map {
                LocalBroadcast.fromConfig(it)
            }
        }

        map
    }

    private val commandRewards: Map<Int, Iterable<CommandReward>> = run {
        val map = mutableMapOf<Int, Iterable<CommandReward>>()

        for (rank in config.getSubsection("rewards.topDamagerCommands").getKeys(false)) {
            val rankRewards = mutableListOf<CommandReward>()

            for (config in config.getSubsections("rewards.topDamagerCommands.$rank")) {
                rankRewards.add(
                    CommandReward(
                        config.getDouble("chance"),
                        config.getStrings("commands")
                    )
                )
            }

            map[rank.toInt()] = rankRewards
        }

        map
    }

    private val nearbyCommandRewardRadius = config.getDouble("rewards.nearbyPlayerCommands.radius")

    private val nearbyCommands: Iterable<CommandReward> = run {
        val list = mutableListOf<CommandReward>()

        for (config in config.getSubsections("rewards.nearbyPlayerCommands.commands")) {
            list.add(
                CommandReward(
                    config.getDouble("chance"),
                    config.getStrings("commands")
                )
            )
        }

        list
    }

    private val xp = XpReward(
        config.getInt("rewards.xp.minimum"),
        config.getInt("rewards.xp.maximum")
    )


    private val drops: Iterable<BossDrop> = run {
        val list = mutableListOf<BossDrop>()

        for (config in config.getSubsections("rewards.drops")) {
            list.add(
                BossDrop(
                    config.getDouble("chance"),
                    config.getStrings("items")
                        .map { Items.lookup(it) }
                        .map { it.item }
                )
            )
        }

        list
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
        val tickers = mutableSetOf(
            LifespanTicker(),
            DisplayNameTicker(),
            TargetTicker()
        )

        if (isBossBarEnabled) {
            tickers.add(
                BossBarTicker(
                    BossBar.bossBar(
                        this.displayName.toComponent(),
                        1.0f,
                        bossBarColor,
                        bossBarStyle
                    )
                )
            )
        }

        return tickers
    }

    fun handleLifecycle(lifecycle: BossLifecycle, location: Location) {
        sounds[lifecycle]?.play(location)
        messages[lifecycle]?.forEach { it.broadcast(location) }
    }

    fun processRewards(player: Player, location: Location) {
        for (drop in drops) {
            drop.drop(location, player)
        }

        xp.drop(location, player)
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
