package com.willfp.illusioner.illusioner.options;

import com.willfp.illusioner.util.internal.OptionedSound;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class GameplayOptions {
    private OptionedSound hitSound;

    private final Set<EffectOption> effectOptions = new HashSet<>();
    private final Set<SummonerOption> summonerOptions = new HashSet<>();

    private boolean shuffle;
    private double shuffleChance;

    private boolean ignoreExplosionDamage;

    public void reload() {

    }

    public OptionedSound getHitSound() {
        return hitSound;
    }

    public Set<EffectOption> getEffectOptions() {
        return effectOptions;
    }

    public Set<SummonerOption> getSummonerOptions() {
        return summonerOptions;
    }

    public double getShuffleChance() {
        return shuffleChance;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isIgnoreExplosionDamage() {
        return ignoreExplosionDamage;
    }

    public static class EffectOption {
        private final double chance;
        private final int level;
        private final PotionEffectType effectType;
        private final int duration;

        public EffectOption(double chance, int level, int duration, PotionEffectType effectType) {
            this.chance = chance;
            this.level = level;
            this.effectType = effectType;
            this.duration = duration;
        }

        public double getChance() {
            return chance;
        }

        public int getLevel() {
            return level;
        }

        public PotionEffectType getEffectType() {
            return effectType;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class SummonerOption {
        private final double chance;
        private final EntityType type;

        public SummonerOption(double chance, EntityType type) {
            this.chance = chance;
            this.type = type;
        }

        public double getChance() {
            return chance;
        }

        public EntityType getType() {
            return type;
        }
    }
}
