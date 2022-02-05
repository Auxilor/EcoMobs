package com.willfp.ecobosses.config

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.ecobosses.EcoBossesPlugin

class EcoBossesYml(plugin: EcoBossesPlugin) : BaseConfig(
    "ecobosses",
    plugin,
    true,
    ConfigType.YAML
)
