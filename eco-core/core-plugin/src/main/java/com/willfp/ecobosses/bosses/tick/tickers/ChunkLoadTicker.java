package com.willfp.ecobosses.bosses.tick.tickers;

import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.tick.BossTicker;
import org.bukkit.Chunk;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ChunkLoadTicker implements BossTicker {
    /**
     * Create new chunk load ticker.
     */
    public ChunkLoadTicker() {
    }

    @Override
    public void tick(@NotNull final EcoBoss boss,
                     @NotNull final LivingEntity entity,
                     final long tick) {
        Chunk chunk = entity.getLocation().getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
    }
}
