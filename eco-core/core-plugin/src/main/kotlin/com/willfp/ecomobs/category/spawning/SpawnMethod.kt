package com.willfp.ecomobs.category.spawning

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.category.MobCategory

abstract class SpawnMethod(
    val category: MobCategory,
    val config: Config
) {
    private var isStarted = false

    fun start() {
        if (isStarted) {
            throw IllegalStateException("Already started!")
        }

        isStarted = true

        onStart()
    }

    fun stop() {
        if (!isStarted) {
            return
        }

        isStarted = false

        onStop()
    }

    protected abstract fun onStart()

    protected abstract fun onStop()
}
