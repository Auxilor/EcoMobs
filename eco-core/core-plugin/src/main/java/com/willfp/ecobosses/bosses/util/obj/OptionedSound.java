package com.willfp.ecobosses.bosses.util.obj;

import org.bukkit.Sound;

public record OptionedSound(Sound sound,
                            float volume,
                            float pitch) {

}
