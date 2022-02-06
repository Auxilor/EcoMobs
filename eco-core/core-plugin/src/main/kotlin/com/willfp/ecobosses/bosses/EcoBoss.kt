package com.willfp.ecobosses.bosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.CustomEntity
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import com.willfp.eco.util.toComponent
import com.willfp.ecobosses.events.BossKillEvent
import com.willfp.ecobosses.lifecycle.BossLifecycle
import com.willfp.ecobosses.tick.BossBarTicker
import com.willfp.ecobosses.tick.BossTicker
import com.willfp.ecobosses.tick.DisplayNameTicker
import com.willfp.ecobosses.tick.LifespanTicker
import com.willfp.ecobosses.tick.TargetTicker
import com.willfp.ecobosses.tick.TeleportHandler
import com.willfp.ecobosses.util.BossDrop
import com.willfp.ecobosses.util.CommandReward
import com.willfp.ecobosses.util.ConfiguredSound
import com.willfp.ecobosses.util.LocalBroadcast
import com.willfp.ecobosses.util.PlayableSound
import com.willfp.ecobosses.util.SpawnTotem
import com.willfp.ecobosses.util.XpReward
import com.willfp.ecobosses.util.topDamagers
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Objects
import java.util.UUID

class EcoBoss(
    val config: Config,
    private val plugin: EcoPlugin
) : Holder {
    val id: String = config.getString("id")

    val displayName: String = config.getString("displayName")

    val lifespan = config.getInt("lifespan")

    val influence = config.getDouble("influence")

    val targetRange = config.getDouble("target.range")

    val targetMode = TargetMode.getByID(config.getString("target.mode"))!!

    val isBossBarEnabled = config.getBool("bossBar.enabled")

    val bossBarRadius = config.getDouble("bossBar.radius")

    val isPreventingMounts = config.getBool("defence.preventMounts")

    val isImmuneToExplosions = config.getBool("defence.explosionImmune")

    val isImmuneToFire = config.getBool("defence.fireImmune")

    val isImmuneToDrowning = config.getBool("defence.drowningImmune")

    val isImmuneToSuffocation = config.getBool("defence.suffocationImmune")

    val meleeDamageMultiplier = config.getDouble("defence.meleeDamageMultiplier")

    val projectileDamageMultiplier = config.getDouble("defence.projectileDamageMultiplier")

    val canTeleport = config.getBool("defence.teleportation.enabled")

    val teleportRange = config.getInt("defence.teleportation.range")

    val teleportInterval = config.getInt("defence.teleportation.interval")

    private val spawnEggBacker: ItemStack? = run {
        val enabled = config.getBool("spawn.egg.enabled")
        if (!enabled) {
            return@run null
        }

        val lookup = Items.lookup(config.getString("spawn.egg.item"))

        if (lookup is EmptyTestableItem) {
            return@run null
        }

        val item = ItemStackBuilder(lookup)
            .addLoreLines(config.getFormattedStrings("spawn.egg.lore"))
            .build().apply { bossEgg = this@EcoBoss }

        val key = plugin.namespacedKeyFactory.create("${this.id}_spawn_egg")

        Items.registerCustomItem(
            key,
            CustomItem(
                key,
                { it.bossEgg == this },
                item
            )
        )

        item
    }

    val spawnEgg: ItemStack?
        get() = this.spawnEggBacker?.clone()

    val recipe: CraftingRecipe? = run {
        if (spawnEggBacker == null || !config.getBool("spawn.egg.craftable")) {
            return@run null
        }

        val recipe = Recipes.createAndRegisterRecipe(
            this@EcoBoss.plugin,
            "${this.id}_spawn_egg",
            spawnEggBacker,
            config.getStrings("spawn.egg.recipe")
        )

        recipe
    }

    val totem: SpawnTotem? = run {
        if (!config.getBool("spawn.totem.enabled")) {
            return@run null
        }

        SpawnTotem(
            Material.getMaterial(config.getString("spawn.totem.top")) ?: return@run null,
            Material.getMaterial(config.getString("spawn.totem.middle")) ?: return@run null,
            Material.getMaterial(config.getString("spawn.totem.bottom")) ?: return@run null
        )
    }

    val disabledTotemWorlds: List<String> = config.getStrings("spawn.totem.notInWorlds")

    val autoSpawnInterval = config.getInt("spawn.autospawn.interval")

    val autoSpawnLocations: List<Location> = run {
        val locations = mutableListOf<Location>()

        for (config in config.getSubsections("spawn.autospawn.locations")) {
            val world = Bukkit.getWorld(config.getString("world")) ?: continue
            val x = config.getDouble("x")
            val y = config.getDouble("y")
            val z = config.getDouble("z")
            locations.add(
                Location(
                    world, x, y, z
                )
            )
        }

        locations
    }

    val spawnConditions = config.getSubsections("spawn.conditions").mapNotNull {
        Conditions.compile(it, "$id Spawn Conditions")
    }

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

    override val conditions = config.getSubsections("conditions").mapNotNull {
        Conditions.compile(it, "Boss ID $id")
    }.toSet()

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

    fun getAllAlive(): Set<LivingEcoBoss> {
        return currentlyAlive.values.toSet()
    }

    fun spawn(location: Location): LivingEcoBoss {
        val mob = mob.spawn(location) as LivingEntity
        val boss = LivingEcoBoss(
            plugin,
            mob.uniqueId,
            this,
            createTickers()
        )
        currentlyAlive[mob.uniqueId] = boss
        return boss
    }

    private fun createTickers(): Set<BossTicker> {
        val tickers = mutableSetOf(
            LifespanTicker(),
            DisplayNameTicker(),
            TargetTicker(),
            TeleportHandler()
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

    fun handleLifecycle(lifecycle: BossLifecycle, location: Location, entity: LivingEntity?) {
        sounds[lifecycle]?.play(location)
        messages[lifecycle]?.forEach { it.broadcast(location, entity?.topDamagers ?: emptyList()) }
    }

    fun processRewards(event: BossKillEvent) {
        val entity = event.boss.entity ?: return
        val location = entity.location
        val player = event.killer

        for (drop in drops) {
            drop.drop(location, player)
        }

        xp.modify(event.event)

        for ((index, damager) in entity.topDamagers.withIndex()) {
            val rewards = commandRewards[index + 1] ?: continue
            val damagerPlayer = Bukkit.getPlayer(damager.uuid) ?: continue
            for (reward in rewards) {
                reward.reward(damagerPlayer)
            }
        }

        for (nearbyPlayer in entity.getNearbyEntities(
            nearbyCommandRewardRadius,
            nearbyCommandRewardRadius,
            nearbyCommandRewardRadius
        ).filterIsInstance<Player>()) {
            for (command in nearbyCommands) {
                command.reward(nearbyPlayer)
            }
        }
    }

    init {
        Entities.registerCustomEntity(
            plugin.namespacedKeyFactory.create(id),
            CustomEntity(
                plugin.namespacedKeyFactory.create(id),
                {
                    if (it !is LivingEntity) {
                        return@CustomEntity false
                    }

                    return@CustomEntity Bosses[it]?.boss == this
                },
                {
                    this.spawn(it).entity
                }
            )
        )
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
