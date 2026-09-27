package com.willfp.ecomobs.mob.stage

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.ViolationContext

abstract class DamageStageModeFactory(override val id: String) : KRegistrable {
    /**
     * The name this mode's progress placeholders are built from, giving `%<name>%`,
     * `%max_<name>%` and `%<name>_percent%`. Null for a mode that needs none.
     *
     * They read as zero, zero and 100 while a mob is in a stage of any other mode.
     */
    open val placeholderName: String? = null

    /**
     * Read one stage of this mode from its [config].
     *
     * [key] is the stage's config key, used only to point config violations at the right
     * place. Throw a [com.willfp.ecomobs.config.ConfigViolationException] to reject the
     * stage: the whole mob is then skipped, with the violation logged.
     */
    abstract fun create(
        config: Config,
        key: Int,
        context: ViolationContext
    ): DamageStageMode
}
