package com.willfp.ecobosses.tick

import com.willfp.ecobosses.bosses.LivingEcoBoss

class ChunkTicker : BossTicker {
    override fun tick(boss: LivingEcoBoss, tick: Int) {
        val currentChunk = boss.chunk

        if (tick % 10 != 0) {
            return
        }

        if (currentChunk.isLoaded && currentChunk.isForceLoaded) {
            return
        }

        currentChunk.load()
        currentChunk.isForceLoaded = true
        boss.forceLoadedChunks.add(currentChunk)
    }

    override fun onDeath(boss: LivingEcoBoss, tick: Int) {
        boss.forceLoadedChunks.forEach { it.isForceLoaded = false }
        boss.forceLoadedChunks.clear()
    }
}
