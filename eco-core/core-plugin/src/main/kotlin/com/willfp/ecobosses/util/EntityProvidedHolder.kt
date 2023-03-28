package com.willfp.ecobosses.util

import com.willfp.libreforge.Holder
import com.willfp.libreforge.ProvidedHolder
import org.bukkit.entity.LivingEntity

class EntityProvidedHolder(
    override val holder: Holder,
    override val item: LivingEntity
) : ProvidedHolder {
    override fun equals(other: Any?): Boolean {
        if (other !is EntityProvidedHolder) {
            return false
        }

        return other.holder == holder && other.item == item
    }

    override fun hashCode(): Int {
        var result = holder.hashCode()
        result = 31 * result + item.hashCode()
        return result
    }
}
