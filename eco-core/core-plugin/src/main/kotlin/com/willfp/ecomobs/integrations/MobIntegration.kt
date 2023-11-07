package com.willfp.ecomobs.integrations

/**
 * A mob integration gives access to a special area of the mob config.
 * This can then be used to access the config of the integration, to apply
 * special plugin-specific behaviours.
 */
class MobIntegration(
    val configKey: String
) {
}
