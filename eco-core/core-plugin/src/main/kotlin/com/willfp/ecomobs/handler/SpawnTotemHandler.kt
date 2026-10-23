package com.willfp.ecomobs.handler

import com.willfp.ecomobs.event.EcoMobTotemBuildEvent
import com.willfp.ecomobs.mob.EcoMob
import com.willfp.ecomobs.mob.EcoMobs
import com.willfp.ecomobs.mob.SpawnReason
import com.willfp.ecomobs.mob.SpawnTotem
import com.willfp.ecomobs.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object SpawnTotemHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockPlaceEvent) {
        val block = event.block
        val blockBelow = block.getRelative(0, -1, 0)
        val blockTwoBelow = block.getRelative(0, -2, 0)
        val blockAbove = block.getRelative(0, 1, 0)
        val blockTwoAbove = block.getRelative(0, 2, 0)

        val player = event.player
        val location = block.location.add(0.0, 1.5, 0.0)

        val totemPositions = listOf(
            listOf(block, blockBelow, blockTwoBelow),
            listOf(blockTwoAbove, blockAbove, block),
            listOf(blockAbove, block, blockBelow)
        )

        for (totemBlocks in totemPositions) {
            val (top, middle, bottom) = totemBlocks
            val totem = SpawnTotem(top.type, middle.type, bottom.type)

            if (EcoMobs.values().any { it.spawnFromTotem(totem, location, player) }) {
                plugin.scheduler.at(block.location).run {
                    totemBlocks.forEach { it.type = Material.AIR }
                }

                return
            }
        }
    }

    private fun EcoMob.spawnFromTotem(totem: SpawnTotem, location: Location, player: Player): Boolean {
        val isBuildable = this.totemOptions?.let { options ->
            options.totem == totem && options.conditions.areMetAndTrigger(
                TriggerData(
                    player = player
                ).dispatch(player.toDispatcher())
            )
        } == true

        if (!isBuildable) {
            return false
        }

        val buildEvent = EcoMobTotemBuildEvent(this, totem, location, player)
        Bukkit.getPluginManager().callEvent(buildEvent)

        return !buildEvent.isCancelled && this.spawn(buildEvent.location, SpawnReason.TOTEM) != null
    }
}
