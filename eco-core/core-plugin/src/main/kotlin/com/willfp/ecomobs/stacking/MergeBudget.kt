package com.willfp.ecomobs.stacking

import java.util.concurrent.atomic.AtomicInteger

/**
 * How many stack merges are left this tick.
 *
 * Merging looks around a mob for a stack to join, so a tick that merges everything it
 * is handed does work in proportion to the mobs standing there - which is how a wall of
 * spawners used to hold the server thread long enough for the watchdog to kill it.
 *
 * The budget puts a ceiling on that: whatever is left over is merged on later ticks
 * instead, and the stacks end up the same, just a moment later.
 *
 * Refilled once a tick by [MobStackTicker], and spent from whichever region thread is
 * doing the merging, so the count is atomic.
 */
object MergeBudget {
    private val remaining = AtomicInteger(0)

    /**
     * Refills the budget, called once per tick.
     */
    fun refill() {
        remaining.set(StackSettings.maxMergesPerTick)
    }

    /**
     * Whether there is anything left to spend, for callers that can stop looking rather
     * than ask for each mob in turn.
     */
    fun hasRemaining(): Boolean =
        StackSettings.maxMergesPerTick <= 0 || remaining.get() > 0

    /**
     * Takes one merge from the budget, or returns false when this tick has had its lot.
     */
    fun take(): Boolean {
        // Zero is no limit at all, for anyone who would rather have the old behaviour.
        if (StackSettings.maxMergesPerTick <= 0) {
            return true
        }

        while (true) {
            val current = remaining.get()

            if (current <= 0) {
                return false
            }

            if (remaining.compareAndSet(current, current - 1)) {
                return true
            }
        }
    }
}
