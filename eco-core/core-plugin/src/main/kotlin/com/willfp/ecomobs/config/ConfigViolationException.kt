package com.willfp.ecomobs.config

import wiki.ecohub.config.ConfigViolation
import wiki.ecohub.config.ViolationContext
import java.lang.Exception

/**
 * Thrown when there is an error in the configuration.
 */
class ConfigViolationException(
    val violation: ConfigViolation,
    val context: (ViolationContext) -> ViolationContext = { it }
): Exception(violation.message)
