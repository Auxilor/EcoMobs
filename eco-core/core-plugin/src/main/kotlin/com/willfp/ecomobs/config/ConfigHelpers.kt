package com.willfp.ecomobs.config

import com.willfp.libreforge.ConfigViolation

class ValidationScope<T>(
    private val value: T,
    private val violated: Boolean
) {
    fun validate(predicate: (T) -> Boolean): ValidationScope<T> {
        return ValidationScope(
            value,
            violated || !predicate(value)
        )
    }

    fun unwrap(violation: () -> ConfigViolation): T {
        if (violated) {
            throw ConfigViolationException(violation())
        }
        return value
    }
}

inline fun <reified T> T.validate(predicate: (T) -> Boolean): ValidationScope<T> {
    return ValidationScope(this, !predicate(this))
}

inline fun <reified T: Any> T?.validateNotNull(violation: ConfigViolation): T {
    return this ?: throw ConfigViolationException(violation)
}

inline fun <reified T> Boolean.ifTrue(block: () -> T): T? {
    return if (this) {
        block()
    } else {
        null
    }
}

fun <T : Enum<T>> T.toConfigKey(): String {
    return this.name.lowercase().replace("_", "-")
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterNotNullValues() = filterValues { it != null } as Map<K, V>
