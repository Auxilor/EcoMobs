package com.willfp.ecomobs.mob.placeholder.impl

import com.willfp.eco.util.toNiceString
import com.willfp.ecomobs.mob.LivingMob
import com.willfp.ecomobs.mob.placeholder.MobPlaceholder
import com.willfp.ecomobs.mob.stage.DamageStageModeFactory

/**
 * The progress placeholders a mode gets from its
 * [placeholder name][DamageStageModeFactory.placeholderName]: `%<name>%` for what is
 * left of the stage, `%max_<name>%` for what it needed, and `%<name>_percent%` for what
 * is left as a percentage.
 *
 * Naming a mode is all it takes to get them, so a new mode never has to write a
 * placeholder of its own.
 */
internal fun DamageStageModeFactory.progressPlaceholders(): List<MobPlaceholder> {
    val name = placeholderName ?: return emptyList()

    return listOf(
        ModeProgressPlaceholder(name, this, fallback = "0") { mob, _ ->
            mob.stageRemaining
        },
        ModeProgressPlaceholder("max_$name", this, fallback = "0") { _, amount ->
            amount
        },
        ModeProgressPlaceholder("${name}_percent", this, fallback = "100") { mob, amount ->
            mob.stageRemaining / amount * 100
        }
    )
}

/**
 * Reads a stage's progress, but only while the mob is in a stage of the mode it belongs
 * to. Any other mode reads as [fallback], so a mob's own mode is the only one its
 * display name can report on.
 */
private class ModeProgressPlaceholder(
    id: String,
    private val factory: DamageStageModeFactory,
    private val fallback: String,
    private val read: (LivingMob, Double) -> Double
) : MobPlaceholder(id) {
    override fun getValue(mob: LivingMob): String {
        val mode = mob.damageStage?.mode ?: return fallback

        if (mode.factory !== factory) {
            return fallback
        }

        return read(mob, mode.amount).toNiceString()
    }
}
