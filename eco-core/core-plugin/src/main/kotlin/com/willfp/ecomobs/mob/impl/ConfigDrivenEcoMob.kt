package com.willfp.ecomobs.mob.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.ai.EntityGoals
import com.willfp.eco.core.entities.ai.TargetGoals
import com.willfp.eco.core.entities.controller
import com.willfp.eco.core.entities.impl.EmptyTestableEntity
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.eco.util.safeNamespacedKeyOf
import com.willfp.eco.util.toComponent
import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.EcoMobsPlugin
import com.willfp.ecomobs.category.MobCategories
import com.willfp.ecomobs.config.ConfigViolationException
import com.willfp.ecomobs.config.filterNotNullValues
import com.willfp.ecomobs.config.ifTrue
import com.willfp.ecomobs.config.toConfigKey
import com.willfp.ecomobs.config.validate
import com.willfp.ecomobs.config.validateNotNull
import com.willfp.ecomobs.event.EcoMobPreSpawnEvent
import com.willfp.ecomobs.event.EcoMobSpawnEvent
import com.willfp.ecomobs.integrations.MobIntegration
import com.willfp.ecomobs.mob.ConfiguredGoal
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.SpawnTotem
import com.willfp.ecomobs.mob.SpawnTotemOptions
import com.willfp.ecomobs.mob.addGoal
import com.willfp.ecomobs.mob.event.MobEvent
import com.willfp.ecomobs.mob.event.MobEvents
import com.willfp.ecomobs.mob.options.BossBarOptions
import com.willfp.ecomobs.mob.options.Drop
import com.willfp.ecomobs.mob.options.MobDrops
import com.willfp.ecomobs.mob.options.SpawnEgg
import com.willfp.ecomobs.mob.options.ecoMobEgg
import com.willfp.ecomobs.tick.TickHandlerBossBar
import com.willfp.ecomobs.tick.TickHandlerDisplayName
import com.willfp.ecomobs.tick.TickHandlerLifespan
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.triggers.DispatchedTrigger
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

val mobKey = namespacedKeyOf("ecomobs", "mob")

internal class ConfigDrivenEcoMob(
    private val plugin: EcoMobsPlugin,
    override val id: String,
    private val config: Config,
    private val context: ViolationContext
) : EcoMob {
    private val trackedMobs = mutableMapOf<UUID, LivingMob>()

    private val onSpawnActions = mutableListOf<(LivingMobImpl) -> Unit>()

    val mob = Entities.lookup(config.getString("mob"))
        .validate { it !is EmptyTestableEntity }
        .unwrap {
            ConfigViolation(
                "mob",
                "Invalid mob",
            )
        }

    override val category = MobCategories[config.getString("category")]
        .validateNotNull(
            ConfigViolation(
                "category",
                "Invalid category"
            )
        )

    val equipment = EquipmentSlot.values().associateWith {
        config.getStringOrNull("equipment.${it.toConfigKey()}")
            ?.let { lookup -> Items.lookup(lookup) }
    }.onSpawn {
        for ((slot, item) in it) {
            @Suppress("UNNECESSARY_SAFE_CALL")
            this.entity.equipment?.setItem(slot, item?.item)
        }
    }

    val isOverridingAI = config.getBool("custom-ai.clear")

    val targetGoals = config.getSubsections("custom-ai.target-goals").mapNotNull {
        val key = safeNamespacedKeyOf(it.getString("key")) ?: throw ConfigViolationException(
            ConfigViolation(
                "key",
                "Invalid goal key"
            )
        ) { ctx ->
            ctx.with("target goals")
        }

        val deserializer = TargetGoals.getByKey(key) ?: throw ConfigViolationException(
            ConfigViolation(
                "key",
                "Invalid target goal"
            )
        ) { ctx ->
            ctx.with("target goals")
        }

        val goal = deserializer.deserialize(it.getSubsection("args")) ?: throw ConfigViolationException(
            ConfigViolation(
                "args",
                "Invalid target goal args"
            )
        ) { ctx ->
            ctx.with("target goals").with(deserializer.key.toString())
        }

        ConfiguredGoal(it.getInt("priority"), goal)
    }

    val entityGoals = config.getSubsections("custom-ai.entity-goals").mapNotNull {
        val key = safeNamespacedKeyOf(it.getString("key")) ?: throw ConfigViolationException(
            ConfigViolation(
                "key",
                "Invalid goal key"
            )
        ) { ctx ->
            ctx.with("entity goals")
        }

        val deserializer = EntityGoals.getByKey(key) ?: throw ConfigViolationException(
            ConfigViolation(
                "key",
                "Invalid entity goal"
            )
        ) { ctx ->
            ctx.with("entity goals")
        }

        val goal = deserializer.deserialize(it.getSubsection("args")) ?: throw ConfigViolationException(
            ConfigViolation(
                "args",
                "Invalid entity goal args"
            )
        ) { ctx ->
            ctx.with("entity goals").with(deserializer.key.toString())
        }

        ConfiguredGoal(it.getInt("priority"), goal)
    }

    val eventEffects = MobEvents.associateWith {
        Effects.compileChain(
            config.getSubsections("effects.${it.configKey}"),
            context.with("effects").with(it.configKey)
        )
    }.filterNotNullValues()

    override val rawDisplayName = config.getString("display-name")

    override val lifespan = config.getInt("lifespan")
        .let { if (it < 1) Int.MAX_VALUE else it * 20 }

    override val canMount = config.getBool("defence.can-mount")

    val damageModifiers = DamageCause.values().associateWith {
        config.getDoubleOrNull("defence.damage-modifiers.${it.name.lowercase()}") ?: 1.0
    }

    val bossBarOptions = config.getBool("boss-bar.enabled")
        .ifTrue {
            val barColor = enumValueOfOrNull<BossBar.Color>(config.getString("boss-bar.color").uppercase())
                .validateNotNull(
                    ConfigViolation(
                        "boss-bar.color",
                        "Invalid boss bar color"
                    )
                )

            val barStyle = enumValueOfOrNull<BossBar.Overlay>(config.getString("boss-bar.style").uppercase())
                .validateNotNull(
                    ConfigViolation(
                        "boss-bar.style",
                        "Invalid boss bar style"
                    )
                )

            val barRadius = config.getDouble("boss-bar.radius")
                .validate { it > 0 }
                .unwrap {
                    ConfigViolation(
                        "boss-bar.radius",
                        "Boss bar radius must be greater than 0"
                    )
                }

            BossBarOptions(
                barColor,
                barStyle,
                barRadius
            )
        }
        .onSpawn {
            val bar = BossBar.bossBar(
                rawDisplayName.toComponent(),
                1f,
                it.color,
                it.style
            )

            this.addTickHandler(TickHandlerBossBar(bar, it))
        }

    val drops = MobDrops(
        config.getInt("drops.experience"),
        config.getSubsections("drops.items").map {
            val items = it.getStrings("items")
                .map { lookup -> Items.lookup(lookup) }
                .filterNot { it is EmptyTestableItem }

            val chance = it.getDouble("chance")

            Drop(chance, items)
        }
    )

    override val spawnEgg = config.getBool("spawn.egg.enabled").ifTrue {
        val conditions = Conditions.compile(
            config.getSubsections("spawn.egg.conditions"),
            context.with("spawn egg conditions")
        )

        val name = config.getString("spawn.egg.name")
        val lore = config.getStrings("spawn.egg.lore")

        val item = Items.lookup(config.getString("spawn.egg.item")).item.apply {
            this.fast().ecoMobEgg = this@ConfigDrivenEcoMob
        }

        CustomItem(
            plugin.createNamespacedKey("${this.id}_spawn_egg"),
            { item.fast().ecoMobEgg == this },
            item
        ).register()

        val isCraftable = config.getBool("spawn.egg.craftable")

        if (isCraftable) {
            Recipes.createAndRegisterRecipe(
                plugin,
                "${this.id}_spawn_egg",
                item,
                config.getStrings("spawn.egg.recipe")
            )
        }

        SpawnEgg(
            this,
            conditions,
            item,
            name,
            lore
        )
    }

    override val totemOptions = config.getBool("spawn.totem.enabled").ifTrue {
        val conditions = Conditions.compile(
            config.getSubsections("spawn.totem.conditions"),
            context.with("spawn totem conditions")
        )

        val top = Items.lookup(config.getString("spawn.totem.top"))
        val middle = Items.lookup(config.getString("spawn.totem.middle"))
        val bottom = Items.lookup(config.getString("spawn.totem.bottom"))

        SpawnTotemOptions(
            conditions,
            SpawnTotem(
                top.item.type,
                middle.item.type,
                bottom.item.type
            )
        )
    }

    /*
    ----------
     */

    private inline fun <T : Any> T?.onSpawn(crossinline block: LivingMobImpl.(T) -> Unit) {
        if (this != null) {
            onSpawnActions += { it.block(this) }
        }
    }

    /*
    ----------
     */

    override fun getIntegrationConfig(integration: MobIntegration): Config {
        return config.getSubsection("integrations.${integration.configKey}")
    }

    override fun getDamageModifier(cause: DamageCause): Double {
        return damageModifiers[cause] ?: 1.0
    }

    override fun canPlayerSpawn(player: Player, spawnReason: SpawnReason, location: Location): Boolean {
        if (spawnReason == SpawnReason.NATURAL) {
            throw IllegalArgumentException("Players cannot spawn mobs naturally")
        }

        return true
    }

    override fun spawnDrops(location: Location, player: Player?) {
        drops.drop(location, player)
    }

    override fun handleEvent(event: MobEvent, trigger: DispatchedTrigger) {
        eventEffects[event]?.trigger(trigger)
    }

    override fun getLivingMob(uuid: UUID): LivingMob? {
        return trackedMobs[uuid]
    }

    @Suppress("UNCHECKED_CAST")
    override fun spawn(location: Location, reason: SpawnReason): LivingMob? {
        // Call bukkit event
        val preSpawnEvent = EcoMobPreSpawnEvent(this, reason)
        Bukkit.getPluginManager().callEvent(preSpawnEvent)

        if (preSpawnEvent.isCancelled) {
            return null
        }

        // Spawn bukkit mob
        val entity = mob.spawn(location) as? Mob ?: throw IllegalStateException("Mob is not a mob")

        // Mark as custom mob
        entity.ecoMob = this

        // Set custom AI
        val controller = entity.controller

        if (isOverridingAI) {
            controller.clearAllGoals()
        }

        for (goal in targetGoals) {
            controller.addGoal(goal)
        }

        for (goal in entityGoals) {
            controller.addGoal(goal)
        }

        // Create living mob
        val livingMob = LivingMobImpl(plugin, this, entity) {
            trackedMobs.remove(entity.uniqueId)
        }

        // Run on-spawn actions
        for (action in onSpawnActions) {
            action(livingMob)
        }

        // Add base tickets
        livingMob.addTickHandler(TickHandlerDisplayName())
        livingMob.addTickHandler(TickHandlerLifespan())

        // Call spawn event
        val spawnEvent = EcoMobSpawnEvent(livingMob, reason)
        Bukkit.getPluginManager().callEvent(spawnEvent)

        // Track mob and start ticking
        trackedMobs[entity.uniqueId] = livingMob
        livingMob.startTicking()
        return livingMob
    }
}

var Mob.ecoMob: EcoMob?
    get() = persistentDataContainer.get(mobKey, PersistentDataType.STRING)
        ?.let { EcoMobs[it] }
    internal set(value) {
        if (value == null) {
            persistentDataContainer.remove(mobKey)
            return
        }

        persistentDataContainer.set(mobKey, PersistentDataType.STRING, value.id)
    }
