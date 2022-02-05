package com.willfp.ecobosses.util

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.Sound
import org.bukkit.entity.Player

data class ConfiguredSound(
    private val sound: Sound,
    private val pitch: Double,
    private val volume: Double
) {
    fun play(player: Player) {
        player.playSound(player.location, sound, volume.toFloat(), pitch.toFloat())
    }

    companion object {
        fun fromConfig(config: Config): ConfiguredSound {
            return ConfiguredSound(
                Sound.valueOf(config.getString("sound").uppercase()),
                config.getDouble("pitch"),
                config.getDouble("volume")
            )
        }
    }
}

data class PlayableSound(
    val sounds: Iterable<ConfiguredSound>
) {
    fun play(player: Player) {
        for (sound in sounds) {
            sound.play(player)
        }
    }
}