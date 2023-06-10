package com.willfp.ecobosses.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler

object MutatorLocationToBoss : Mutator<NoCompileData>("location_to_boss") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val entity = data.holder.getProvider<LivingEntity>() ?: return data
        val location = entity.location

        return data.copy(
            location = location
        )
    }
}
