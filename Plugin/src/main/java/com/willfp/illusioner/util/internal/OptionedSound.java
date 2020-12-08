package com.willfp.illusioner.util.internal;

import org.bukkit.Sound;

public class OptionedSound {
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final boolean broadcast;

    public OptionedSound(Sound sound, float volume, float pitch, boolean broadcast) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.broadcast = broadcast;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isBroadcast() {
        return broadcast;
    }
}
