package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.scheduled.ScheduledPoint
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions

private val pointIdPattern = Regex("[a-z0-9_]+")

/**
 * The options for spawning a mob at fixed points on a timer.
 */
class ScheduledSpawn(
    val points: List<ScheduledPoint>,
    private val interval: IntRange,
    val conditions: ConditionList,
    val broadcast: List<String>
) {
    /**
     * The epoch millisecond a spawn or death happening now should delay the next spawn until.
     */
    fun nextDue(): Long = System.currentTimeMillis() + interval.random() * 1000L

    companion object {
        /**
         * Parse the options from the `spawn.scheduled` section of the mob [mobId]'s [config].
         */
        fun parse(mobId: String, config: Config, context: ViolationContext): ScheduledSpawn {
            val scheduledContext = context.with("scheduled spawn")

            return ScheduledSpawn(
                parsePoints(mobId, config, scheduledContext),
                parseInterval(config, scheduledContext),
                Conditions.compile(
                    config.getSubsections("spawn.scheduled.conditions"),
                    context.with("scheduled spawn conditions")
                ),
                config.getStrings("spawn.scheduled.broadcast")
            )
        }

        private fun parsePoints(
            mobId: String,
            config: Config,
            context: ViolationContext
        ): List<ScheduledPoint> {
            val defaultMaxAlive = parseMaxAlive(config.getIntOrNull("spawn.scheduled.max-alive"), 1, context)
            val points = mutableListOf<ScheduledPoint>()

            for (pointConfig in config.getSubsections("spawn.scheduled.points")) {
                val id = pointConfig.getStringOrNull("id")
                val world = pointConfig.getStringOrNull("world")
                val x = pointConfig.getDoubleOrNull("x")
                val y = pointConfig.getDoubleOrNull("y")
                val z = pointConfig.getDoubleOrNull("z")

                if (id == null || world == null || x == null || y == null || z == null) {
                    context.log(ConfigViolation("points", "Point ${id ?: "without an id"} needs id, world, x, y and z"))
                    continue
                }

                if (id == "all") {
                    context.log(ConfigViolation("points", "Point id all is reserved"))
                    continue
                }

                if (!id.matches(pointIdPattern)) {
                    context.log(ConfigViolation("points", "Point id $id may only contain a-z, 0-9 and _"))
                    continue
                }

                if (points.any { it.id == id }) {
                    context.log(ConfigViolation("points", "Duplicate point id $id"))
                    continue
                }

                val maxAlive = parseMaxAlive(pointConfig.getIntOrNull("max-alive"), defaultMaxAlive, context)

                points += ScheduledPoint(mobId, id, world, x, y, z, maxAlive)
            }

            if (points.isEmpty()) {
                context.log(ConfigWarning("points", "No valid points, nothing will spawn"))
            }

            return points
        }

        private fun parseMaxAlive(value: Int?, default: Int, context: ViolationContext): Int {
            if (value == null) {
                return default
            }

            if (value < 1) {
                context.log(ConfigViolation("max-alive", "Max alive must be at least 1, using 1"))
                return 1
            }

            return value
        }

        private fun parseInterval(config: Config, context: ViolationContext): IntRange {
            val min = config.getIntOrNull("spawn.scheduled.interval.min")
            val max = config.getIntOrNull("spawn.scheduled.interval.max")

            if (min == null && max == null) {
                context.log(ConfigViolation("interval", "Interval needs min and max, using 1800 seconds"))
                return 1800..1800
            }

            var low = min ?: max!!
            var high = max ?: min!!

            if (low < 0 || high < 0) {
                context.log(ConfigViolation("interval", "Interval cannot be negative, using 0 instead"))
                low = low.coerceAtLeast(0)
                high = high.coerceAtLeast(0)
            }

            if (low > high) {
                context.log(ConfigViolation("interval", "Interval min is above max, using min for both"))
                high = low
            }

            return low..high
        }
    }
}
