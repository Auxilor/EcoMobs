package com.willfp.ecomobs.stacking

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.setClientsideDisplayName
import com.willfp.eco.util.toComponent
import com.willfp.ecomobs.mob.impl.ecoMob
import com.willfp.ecomobs.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

private const val NAMEPLATE_RATE = 5
private const val NAMEPLATE_RADIUS = 20.0
private const val SWEEP_RADIUS = 48.0

/**
 * Paints stack nameplates, and sweeps for mobs that drifted together after spawning.
 */
object MobStackTicker {
    private var task: EcoTask? = null
    private var tick = 0

    fun start() {
        stop()

        if (!StackSettings.enabled) {
            return
        }

        tick = 0
        task = plugin.scheduler.global().runTimer(1, 1) { tick() }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun tick() {
        val nameplates = tick % NAMEPLATE_RATE == 0
        val sweep = tick % StackSettings.sweepRate == 0

        tick++

        if (!nameplates && !sweep) {
            return
        }

        for (player in Bukkit.getOnlinePlayers()) {
            // The work touches entities, so it's submitted to the player's own context
            // rather than run from the global one.
            plugin.scheduler.on(player).run {
                if (nameplates) {
                    renderNameplates(player)
                }

                if (sweep) {
                    sweepAround(player)
                }
            }
        }
    }

    private fun renderNameplates(player: Player) {
        for (mob in nearbyMobs(player, NAMEPLATE_RADIUS)) {
            if (!mob.stack.isStacked) {
                continue
            }

            mob.setClientsideDisplayName(player, nameplateFor(mob, player).toComponent(), true)
        }
    }

    private fun sweepAround(player: Player) {
        for (mob in nearbyMobs(player, SWEEP_RADIUS)) {
            MobStacks.tryMerge(mob)
        }
    }

    private fun nearbyMobs(player: Player, radius: Double): List<Mob> =
        player.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<Mob>()

    // Rendered per player, so the nameplate can carry their placeholders too.
    private fun nameplateFor(mob: Mob, player: Player): String =
        StackSettings.nameplate
            .replace("%size%", mob.stack.size.toString())
            .replace("%name%", displayNameOf(mob))
            .formatEco(player, true)

    private fun displayNameOf(mob: Mob): String {
        val living = mob.ecoMob?.getLivingMob(mob)

        return living?.displayName ?: mob.name
    }
}
