package com.willfp.ecobosses.lifecycle

enum class BossLifecycle(
    val isDeath: Boolean
) {
    SPAWN(false),
    KILL(true),
    DESPAWN(true),
    INJURE(false)
}
