package com.willfp.ecobosses.bosses.util.obj;

import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record OptionedSound(Sound sound,
                            float volume,
                            float pitch) {

}
