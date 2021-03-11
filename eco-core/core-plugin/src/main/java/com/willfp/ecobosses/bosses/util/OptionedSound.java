package com.willfp.ecobosses.bosses.util;

import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class OptionedSound {
    /**
     * The sound.
     */
    @Getter
    private final Sound sound;

    /**
     * The volume.
     */
    @Getter
    private final float volume;

    /**
     * The pitch, from 0.5 to 2.
     */
    @Getter
    private final float pitch;

    /**
     * Create a new optioned sound.
     *
     * @param sound     The sound.
     * @param volume    The volume.
     * @param pitch     The pitch, from 0.5 to 2.
     */
    public OptionedSound(@NotNull final Sound sound,
                         final float volume,
                         final float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
}
