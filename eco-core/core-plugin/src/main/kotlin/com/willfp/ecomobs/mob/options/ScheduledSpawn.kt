package com.willfp.ecomobs.mob.options

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecomobs.scheduled.ScheduledPoint
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import org.bukkit.util.Vector
import java.util.concurrent.TimeUnit

private val pointIdPattern = Regex("[a-z0-9_]+")

private const val DEFAULT_INTERVAL_SECONDS = 1800

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
    fun nextDue(): Long = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(interval.random().toLong())

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
            val defaultMaxAlive = parseMaxAlive(
                value = config.getIntOrNull("spawn.scheduled.max-alive"),
                default = 1,
                context = context
            )
            val points = mutableListOf<ScheduledPoint>()

            for (pointConfig in config.getSubsections("spawn.scheduled.points")) {
                parsePoint(mobId, pointConfig, points, defaultMaxAlive, context)?.let { points += it }
            }

            if (points.isEmpty()) {
                context.log(ConfigWarning(param = "points", message = "No valid points, nothing will spawn"))
            }

            return points
        }

        private fun parsePoint(
            mobId: String,
            pointConfig: Config,
            existing: List<ScheduledPoint>,
            defaultMaxAlive: Int,
            context: ViolationContext
        ): ScheduledPoint? {
            val id = pointConfig.getStringOrNull("id")
            val world = pointConfig.getStringOrNull("world")
            val position = listOf("x", "y", "z").mapNotNull { pointConfig.getDoubleOrNull(it) }
                .takeIf { it.size == 3 }
                ?.let { (x, y, z) -> Vector(x, y, z) }

            return when {
                id == null || world == null || position == null -> {
                    invalidPoint(context, "Point ${id ?: "without an id"} needs id, world, x, y and z")
                }
                id == "all" -> {
                    invalidPoint(context, "Point id all is reserved")
                }
                !id.matches(pointIdPattern) -> {
                    invalidPoint(context, "Point id $id may only contain a-z, 0-9 and _")
                }
                existing.any { it.id == id } -> {
                    invalidPoint(context, "Duplicate point id $id")
                }
                else -> {
                    val maxAlive = parseMaxAlive(
                        value = pointConfig.getIntOrNull("max-alive"),
                        default = defaultMaxAlive,
                        context = context
                    )
                    ScheduledPoint(mobId, id, world, position, maxAlive)
                }
            }
        }

        private fun invalidPoint(context: ViolationContext, message: String): ScheduledPoint? {
            context.log(ConfigViolation(param = "points", message = message))
            return null
        }

        private fun parseMaxAlive(value: Int?, default: Int, context: ViolationContext): Int = when {
            value == null -> {
                default
            }
            value < 1 -> {
                context.log(ConfigViolation(param = "max-alive", message = "Max alive must be at least 1, using 1"))
                1
            }
            else -> {
                value
            }
        }

        private fun parseInterval(config: Config, context: ViolationContext): IntRange {
            val min = config.getIntOrNull("spawn.scheduled.interval.min")
            val max = config.getIntOrNull("spawn.scheduled.interval.max")

            if (min == null && max == null) {
                context.log(
                    ConfigViolation(param = "interval", message = "Interval needs min and max, using 1800 seconds")
                )
                return DEFAULT_INTERVAL_SECONDS..DEFAULT_INTERVAL_SECONDS
            }

            var low = min ?: max ?: DEFAULT_INTERVAL_SECONDS
            var high = max ?: min ?: DEFAULT_INTERVAL_SECONDS

            if (low < 0 || high < 0) {
                context.log(
                    ConfigViolation(param = "interval", message = "Interval cannot be negative, using 0 instead")
                )
                low = low.coerceAtLeast(0)
                high = high.coerceAtLeast(0)
            }

            if (low > high) {
                context.log(
                    ConfigViolation(param = "interval", message = "Interval min is above max, using min for both")
                )
                high = low
            }

            return low..high
        }
    }
}
