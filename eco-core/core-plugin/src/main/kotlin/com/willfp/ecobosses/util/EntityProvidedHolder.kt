package com.willfp.ecobosses.util

import com.willfp.libreforge.Holder
import com.willfp.libreforge.ProvidedHolder
import org.bukkit.entity.LivingEntity

class EntityProvidedHolder(
    override val holder: Holder,
    override val provider: LivingEntity
) : ProvidedHolder {
    override fun equals(other: Any?): Boolean {
        if (other !is EntityProvidedHolder) {
            return false
        }

        return other.holder == holder && other.provider == provider
    }

    override fun hashCode(): Int {
        var result = holder.hashCode()
        result = 31 * result + provider.hashCode()
        return result
    }
}
